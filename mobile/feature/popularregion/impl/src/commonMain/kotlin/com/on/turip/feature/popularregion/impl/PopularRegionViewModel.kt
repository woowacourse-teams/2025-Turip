package com.on.turip.feature.popularregion.impl

import androidx.lifecycle.viewModelScope
import com.on.turip.core.data.session.SessionManager
import com.on.turip.core.domain.repository.ContentRepository
import com.on.turip.core.domain.repository.RegionRepository
import com.on.turip.core.model.content.PagedContentsResult
import com.on.turip.core.model.region.RegionCategory
import com.on.turip.core.model.region.RegionPopularity
import com.on.turip.core.model.region.RegionVisitor
import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.ui.BaseViewModel
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.core.ui.error.UiError
import com.on.turip.core.ui.error.toUiError
import com.on.turip.feature.popularregion.impl.map.SidoAreas
import com.on.turip.feature.popularregion.impl.model.PopularRegionModel
import com.on.turip.feature.popularregion.impl.model.RegionContentModel
import com.on.turip.feature.popularregion.impl.model.RegionContentsUiState
import com.on.turip.feature.popularregion.impl.model.toUiModel
import io.github.aakira.napier.Napier
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

/**
 * 인기 관광지 지도의 상태 소유자.
 *
 * 지도 선택 상태의 단일 진실 공급원은 [PopularRegionState.selectedRegionCode] 하나다.
 * 바텀시트는 이 값을 따라 열리고 닫힐 뿐, 스스로 상태를 갖지 않는다.
 *
 * 지도에 칠하는 값은 방문자 수 API(최근 한 달, 시도 17개)에서 오고,
 * 시트 안 영상 목록은 기존 콘텐츠 API에서 온다. 두 API의 지역 단위가 달라
 * 시도 ↔ 지역 카테고리는 [SidoAreas] 가 이어준다.
 */
class PopularRegionViewModel(
    private val regionRepository: RegionRepository,
    private val contentRepository: ContentRepository,
    private val sessionManager: SessionManager,
) : BaseViewModel<PopularRegionIntent, PopularRegionState, PopularRegionEffect>(
        PopularRegionState(),
    ) {
    /** 같은 지역을 다시 눌렀을 때 또 요청하지 않도록 화면 수명 동안 기억한다. */
    private val cachedContents: MutableMap<String, ImmutableList<RegionContentModel>> =
        mutableMapOf()

    private var loadJob: Job? = null
    private var contentsJob: Job? = null

    init {
        loadRegions()
    }

    override fun onIntent(intent: PopularRegionIntent) {
        when (intent) {
            is PopularRegionIntent.SelectRegion -> selectRegion(intent.regionCode)
            PopularRegionIntent.DismissSheet -> clearSelection()
            PopularRegionIntent.ClickRelatedContents -> navigateToRelatedContents()
            is PopularRegionIntent.ClickContent ->
                emitEffect(PopularRegionEffect.NavigateToTripDetail(intent.contentId))

            PopularRegionIntent.RetryLoad -> loadRegions()
            PopularRegionIntent.RetryContents -> retryContents()
        }
    }

    /**
     * 방문자 수와 지역 카테고리를 함께 받아 지도를 만든다.
     *
     * 카테고리 조회가 실패해도 지도는 그린다. 그 경우 모든 지역이 `콘텐츠 없음`으로 보일 뿐이라
     * 지도 자체를 막을 이유가 없다.
     */
    private fun loadRegions() {
        loadJob?.cancel()
        contentsJob?.cancel()
        cachedContents.clear()
        loadJob =
            viewModelScope.launch {
                updateState {
                    PopularRegionState(isLoading = true)
                }

                val popularityDeferred: Deferred<TuripResult<RegionPopularity>> =
                    async { regionRepository.loadRegionPopularity() }
                val categoriesDeferred: Deferred<TuripResult<List<RegionCategory>>> =
                    async { regionRepository.loadRegionCategories(IS_DOMESTIC) }

                val popularityResult: TuripResult<RegionPopularity> = popularityDeferred.await()
                val categoriesResult: TuripResult<List<RegionCategory>> = categoriesDeferred.await()

                val popularity: RegionPopularity =
                    when (popularityResult) {
                        is TuripResult.Success -> popularityResult.value
                        is TuripResult.Failure -> {
                            Napier.e("인기 관광지 - 방문자 수 조회 실패", popularityResult.cause)
                            handleLoadError(popularityResult)
                            return@launch
                        }
                    }

                if (categoriesResult is TuripResult.Failure) {
                    Napier.w("인기 관광지 - 지역 카테고리 조회 실패, 콘텐츠 없이 지도만 그린다", categoriesResult.cause)
                }

                val regionCategoryNames: Map<Int, ImmutableList<String>> =
                    (categoriesResult as? TuripResult.Success)
                        ?.value
                        .orEmpty()
                        .groupBy { SidoAreas.areaCodeOf(it.name) }
                        .mapNotNull { (areaCode, categories) ->
                            areaCode?.let { it to categories.map(RegionCategory::name).toImmutableList() }
                        }.toMap()

                // 통합시와 그 구성 지역이 함께 내려오면 같은 땅이 여러 조각으로 나뉘고
                // 방문자 수도 중복 집계된다. 통합시 쪽을 남기고 구성 지역을 접는다.
                val supersededAreaCodes: Set<Int> =
                    SidoAreas.supersededAreaCodes(
                        popularity.regions.map(RegionVisitor::areaCode).toSet(),
                    )
                val visitors: List<RegionVisitor> =
                    popularity.regions.filterNot { it.areaCode in supersededAreaCodes }

                if (supersededAreaCodes.isNotEmpty()) {
                    Napier.w("인기 관광지 - 통합시에 흡수된 시도 코드 $supersededAreaCodes 를 지도에서 제외")
                }

                // 행정구역 개편으로 새 코드가 생기면 지도에 놓을 자리를 몰라 빠진다.
                // 조용히 사라지면 알아챌 방법이 없으므로 남는 코드를 남겨 둔다.
                val unmappedVisitors: List<RegionVisitor> =
                    visitors.filter { SidoAreas.areaOf(it.areaCode) == null }
                if (unmappedVisitors.isNotEmpty()) {
                    Napier.w(
                        "인기 관광지 - 지도에 없는 시도 코드 " +
                            unmappedVisitors.joinToString { "${it.areaCode}(${it.name})" },
                    )
                }

                val regions: ImmutableList<PopularRegionModel> =
                    visitors
                        .mapNotNull { visitor ->
                            visitor.toUiModel(
                                regionCategoryNames = regionCategoryNames[visitor.areaCode]
                                    ?: persistentListOf(),
                            )
                        }.toImmutableList()

                Napier.d("인기 관광지 - 기준월 ${popularity.baseMonth}, 지역 ${regions.size}개 로드")

                updateState {
                    copy(
                        baseMonth = popularity.baseMonth,
                        regions = regions,
                        selectedRegionCode = null,
                        isLoading = false,
                        errorUiState =
                            if (regions.isEmpty()) ErrorUiState.Unexpected else ErrorUiState.None,
                    )
                }
            }
    }

    private fun selectRegion(regionCode: String) {
        if (regionCode == currentState.selectedRegionCode) return

        val region: PopularRegionModel =
            currentState.regions.firstOrNull { it.code == regionCode } ?: return

        updateState { copy(selectedRegionCode = regionCode) }
        loadContents(region)
    }

    private fun clearSelection() {
        if (currentState.selectedRegionCode == null) return
        contentsJob?.cancel()
        updateState { copy(selectedRegionCode = null) }
    }

    /**
     * 시도 하나에 지역 카테고리가 여럿 붙을 수 있어(강원 → 강릉·속초) 한 번에 모아 온다.
     * 카테고리 하나가 실패하면 그 지역만 빠지고, 전부 실패했을 때만 에러로 본다.
     */
    private fun loadContents(region: PopularRegionModel) {
        contentsJob?.cancel()

        if (!region.hasRegionCategory) {
            updateState { copy(contentsUiState = RegionContentsUiState.Unsupported) }
            return
        }

        cachedContents[region.code]?.let { cached ->
            updateState { copy(contentsUiState = RegionContentsUiState.Success(cached)) }
            return
        }

        contentsJob =
            viewModelScope.launch {
                updateState { copy(contentsUiState = RegionContentsUiState.Loading) }

                val results: List<TuripResult<PagedContentsResult>> =
                    region.regionCategoryNames
                        .map { categoryName ->
                            async {
                                contentRepository.loadContentsByRegion(
                                    regionCategoryName = categoryName,
                                    size = CONTENTS_PAGE_SIZE,
                                    lastId = INITIAL_LAST_ID,
                                )
                            }
                        }.awaitAll()

                results.filterIsInstance<TuripResult.Failure>().forEach { failure ->
                    Napier.w("인기 관광지 - ${region.name} 콘텐츠 조회 실패", failure.cause)
                }

                val tokenExpired: Boolean =
                    results
                        .filterIsInstance<TuripResult.Failure>()
                        .any { it.errorType.toUiError() == UiError.Global.TokenExpired }
                if (tokenExpired) {
                    navigateToLogin()
                    return@launch
                }

                val successes: List<PagedContentsResult> =
                    results
                        .filterIsInstance<TuripResult.Success<PagedContentsResult>>()
                        .map { it.value }

                if (successes.isEmpty()) {
                    updateState { copy(contentsUiState = RegionContentsUiState.Error) }
                    return@launch
                }

                val contents: ImmutableList<RegionContentModel> =
                    successes
                        .flatMap { it.videos }
                        .map { it.toUiModel() }
                        .distinctBy { it.contentId }
                        .take(CONTENTS_PAGE_SIZE)
                        .toImmutableList()

                cachedContents[region.code] = contents
                Napier.d("인기 관광지 - ${region.name} 콘텐츠 ${contents.size}개 로드")

                updateState { copy(contentsUiState = RegionContentsUiState.Success(contents)) }
            }
    }

    private fun retryContents() {
        val region: PopularRegionModel = currentState.selectedRegion ?: return
        loadContents(region)
    }

    private fun navigateToRelatedContents() {
        val regionCategoryName: String =
            currentState.selectedRegion?.primaryRegionCategoryName ?: return
        emitEffect(PopularRegionEffect.NavigateToRegionResult(regionCategoryName))
    }

    private fun handleLoadError(failure: TuripResult.Failure) {
        when (failure.errorType.toUiError()) {
            UiError.Global.Network ->
                updateState { copy(isLoading = false, errorUiState = ErrorUiState.Network) }

            UiError.Global.Server ->
                updateState { copy(isLoading = false, errorUiState = ErrorUiState.Server) }

            UiError.Global.TokenExpired -> navigateToLogin()

            else -> updateState { copy(isLoading = false, errorUiState = ErrorUiState.Unexpected) }
        }
    }

    private fun navigateToLogin() {
        viewModelScope.launch {
            sessionManager.switchToGuest()
            emitEffect(PopularRegionEffect.NavigateToLogin)
        }
    }

    companion object {
        private const val IS_DOMESTIC: Boolean = true
        private const val CONTENTS_PAGE_SIZE: Int = 10
        private const val INITIAL_LAST_ID: Long = 0L
    }
}
