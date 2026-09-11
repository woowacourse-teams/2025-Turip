package com.on.turip.feature.popularregion.impl

import androidx.lifecycle.viewModelScope
import com.on.turip.core.data.session.SessionManager
import com.on.turip.core.domain.repository.ContentRepository
import com.on.turip.core.domain.repository.RegionRepository
import com.on.turip.core.model.content.PagedContentsResult
import com.on.turip.core.model.region.DestinationVisitor
import com.on.turip.core.model.region.PopularDestination
import com.on.turip.core.model.region.RegionPopularity
import com.on.turip.core.model.region.RegionVisitor
import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.ui.BaseViewModel
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.core.ui.error.UiError
import com.on.turip.core.ui.error.toUiError
import com.on.turip.feature.popularregion.impl.map.PopularDestinationShapes
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
import kotlinx.coroutines.launch

/**
 * 인기 관광지 지도의 상태 소유자.
 *
 * 지도 선택 상태의 단일 진실 공급원은 [PopularRegionState.selectedRegionCode] 하나다.
 * 바텀시트는 이 값을 따라 열리고 닫힐 뿐, 스스로 상태를 갖지 않는다.
 *
 * 지도는 두 층이다. 아래층은 시도 방문자 수 API(최근 한 달, 시도 17개)로 국토를 빈 칸 없이 칠하고,
 * 위층은 인기 관광지 API(튜립 지역 카테고리 14곳 중 상위 10곳)로 순위를 얹는다.
 * 시트 안 영상 목록은 위층의 지역 카테고리로 기존 콘텐츠 API에서 가져온다.
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
            is PopularRegionIntent.SelectRegion -> {
                selectRegion(intent.regionCode)
            }

            PopularRegionIntent.DismissSheet -> {
                clearSelection()
            }

            PopularRegionIntent.ClickRelatedContents -> {
                navigateToRelatedContents()
            }

            is PopularRegionIntent.ClickContent -> {
                emitEffect(PopularRegionEffect.NavigateToTripDetail(intent.contentId))
            }

            PopularRegionIntent.RetryLoad -> {
                loadRegions()
            }

            PopularRegionIntent.RetryContents -> {
                retryContents()
            }
        }
    }

    /**
     * 방문자 수와 지역 카테고리를 함께 받아 지도를 만든다.
     *
     * 카테고리 조회가 실패해도 지도는 그린다. 그 경우 모든 지역이 `콘텐츠 없음`으로 보일 뿐이라
     * 지도 자체를 막을 이유가 없다.
     *
     * 상단 칩은 관광지 층만 있으면 그릴 수 있고 그 응답은 홈에서 이미 받아 캐시돼 있다.
     * 그래서 두 응답을 함께 기다리지 않고, 관광지 층이 오는 대로 먼저 반영한다.
     * 같이 기다리면 칩이 아직 캐시되지 않은 시도 방문자 수를 따라 늦게 나타난다.
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
                val destinationsDeferred: Deferred<TuripResult<PopularDestination>> =
                    async { regionRepository.loadPopularDestinations() }

                val destinationsResult: TuripResult<PopularDestination> = destinationsDeferred.await()

                // 위층이 없어도 국토는 칠할 수 있다. 배경만 남을 뿐이라 지도를 막을 이유가 없다.
                if (destinationsResult is TuripResult.Failure) {
                    Napier.w("인기 관광지 - 관광지 순위 조회 실패, 시도 층만 그린다", destinationsResult.cause)
                }

                val destinations: List<PopularRegionModel> =
                    (destinationsResult as? TuripResult.Success)
                        ?.value
                        ?.destinations
                        .orEmpty()
                        .toDestinationModels()

                // 칩만 먼저 세운다. isLoading 이 아직 true 라 지도는 그대로 비어 있다.
                if (destinations.isNotEmpty()) {
                    updateState { copy(regions = destinations.toImmutableList()) }
                }

                val popularityResult: TuripResult<RegionPopularity> = popularityDeferred.await()
                val popularity: RegionPopularity =
                    when (popularityResult) {
                        is TuripResult.Success -> {
                            popularityResult.value
                        }

                        is TuripResult.Failure -> {
                            Napier.e("인기 관광지 - 시도 방문자 수 조회 실패", popularityResult.cause)
                            // 먼저 세워 둔 칩을 거둔다. 에러 화면이 지도를 대신하는 동안
                            // 칩을 눌러도 열릴 시트가 없어 누를 수 있는 것처럼 보이면 안 된다.
                            updateState { copy(regions = persistentListOf()) }
                            handleLoadError(popularityResult)
                            return@launch
                        }
                    }

                updateState {
                    copy(
                        baseMonth = popularity.baseMonth,
                        regions =
                            (popularity.toSidoModels(destinations) + destinations)
                                .sortedByDescending { it.visitorCount }
                                .toImmutableList(),
                        selectedRegionCode = null,
                        isLoading = false,
                        errorUiState = ErrorUiState.None,
                    )
                }
            }
    }

    /**
     * 아래층(시도)을 만든다.
     *
     * 시도 하나가 통째로 인기 관광지인 곳(서울·부산 등 6곳)은 위층이 같은 땅을 덮으므로 여기서 뺀다.
     * 남겨 두면 같은 폴리곤을 두 번 그리게 되고, 탭도 어느 쪽이 잡힐지 순서에 기대게 된다.
     */
    private fun RegionPopularity.toSidoModels(
        destinations: List<PopularRegionModel>,
    ): List<PopularRegionModel> {
        // 통합시와 그 구성 지역이 함께 내려오면 같은 땅이 여러 조각으로 나뉘고
        // 방문자 수도 중복 집계된다. 통합시 쪽을 남기고 구성 지역을 접는다.
        val supersededAreaCodes: Set<Int> =
            SidoAreas.supersededAreaCodes(regions.map(RegionVisitor::areaCode).toSet())
        if (supersededAreaCodes.isNotEmpty()) {
            Napier.w("인기 관광지 - 통합시에 흡수된 시도 코드 $supersededAreaCodes 를 지도에서 제외")
        }

        val coveredAreaCodes: Set<Int> =
            destinations
                .mapNotNull { destination ->
                    destination.regionCategoryName
                        ?.let { PopularDestinationShapes.destinationOf(it) }
                        ?.sidoAreaCode
                }.toSet()

        val visitors: List<RegionVisitor> =
            regions.filterNot { it.areaCode in supersededAreaCodes || it.areaCode in coveredAreaCodes }

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

        return visitors.mapNotNull { it.toUiModel() }
    }

    /**
     * 위층(인기 관광지)을 만든다.
     *
     * 서버가 좌표를 주지 않아 지도에 놓을 자리는 [PopularDestinationShapes] 가 갖고 있다.
     * 후보 14곳은 서버가 정하므로, 표에 없는 이름이 새로 생기면 조용히 사라지지 않게 경고를 남긴다.
     */
    private fun List<DestinationVisitor>.toDestinationModels(): List<PopularRegionModel> {
        val unmapped: List<DestinationVisitor> =
            filter { it.regionCategoryName !in PopularDestinationShapes.regionCategoryNames }
        if (unmapped.isNotEmpty()) {
            Napier.w(
                "인기 관광지 - 지도에 없는 지역 카테고리 " +
                    unmapped.joinToString { it.regionCategoryName },
            )
        }

        return mapNotNull { it.toUiModel() }
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
     * 선택한 지역의 연관 콘텐츠를 가져온다.
     *
     * 지역 카테고리가 붙는 것은 위층(인기 관광지)뿐이라 호출은 언제나 한 번이다.
     * 아래층 시도를 눌렀을 때는 방문자 수만 보여 준다.
     */
    private fun loadContents(region: PopularRegionModel) {
        contentsJob?.cancel()

        val regionCategoryName: String = region.regionCategoryName ?: run {
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

                val result: TuripResult<PagedContentsResult> =
                    contentRepository.loadContentsByRegion(
                        regionCategoryName = regionCategoryName,
                        size = CONTENTS_PAGE_SIZE,
                        lastId = INITIAL_LAST_ID,
                    )

                val paged: PagedContentsResult =
                    when (result) {
                        is TuripResult.Success -> {
                            result.value
                        }

                        is TuripResult.Failure -> {
                            Napier.w("인기 관광지 - ${region.name} 콘텐츠 조회 실패", result.cause)
                            if (result.errorType.toUiError() == UiError.Global.TokenExpired) {
                                navigateToLogin()
                            } else {
                                updateState { copy(contentsUiState = RegionContentsUiState.Error) }
                            }
                            return@launch
                        }
                    }

                val contents: ImmutableList<RegionContentModel> =
                    paged.videos
                        .map { it.toUiModel() }
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
        val region: PopularRegionModel = currentState.selectedRegion ?: return
        val regionCategoryName: String = region.regionCategoryName ?: return
        emitEffect(
            PopularRegionEffect.NavigateToRegionBriefing(
                regionCategoryName = regionCategoryName,
                visitorCount = region.visitorCount,
                baseMonth = currentState.baseMonth,
            ),
        )
    }

    private fun handleLoadError(failure: TuripResult.Failure) {
        when (failure.errorType.toUiError()) {
            UiError.Global.Network -> {
                updateState { copy(isLoading = false, errorUiState = ErrorUiState.Network) }
            }

            UiError.Global.Server -> {
                updateState { copy(isLoading = false, errorUiState = ErrorUiState.Server) }
            }

            UiError.Global.TokenExpired -> {
                navigateToLogin()
            }

            else -> {
                updateState { copy(isLoading = false, errorUiState = ErrorUiState.Unexpected) }
            }
        }
    }

    private fun navigateToLogin() {
        viewModelScope.launch {
            sessionManager.switchToGuest()
            emitEffect(PopularRegionEffect.NavigateToLogin)
        }
    }

    companion object {
        private const val CONTENTS_PAGE_SIZE: Int = 10
        private const val INITIAL_LAST_ID: Long = 0L
    }
}
