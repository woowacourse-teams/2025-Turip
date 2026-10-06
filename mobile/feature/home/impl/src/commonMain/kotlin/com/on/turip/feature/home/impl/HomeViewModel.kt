package com.on.turip.feature.home.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.core.data.session.SessionManager
import com.on.turip.core.domain.repository.ArticleRepository
import com.on.turip.core.domain.repository.ContentRepository
import com.on.turip.core.domain.repository.RegionRepository
import com.on.turip.core.model.article.ArticlesResult
import com.on.turip.core.model.content.UsersLikeContent
import com.on.turip.core.model.region.DestinationVisitor
import com.on.turip.core.model.region.PopularDestination
import com.on.turip.core.model.region.RegionCategory
import com.on.turip.core.model.result.ErrorType
import com.on.turip.core.model.result.onFailure
import com.on.turip.core.model.result.onSuccess
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.core.ui.error.UiError
import com.on.turip.core.ui.error.toUiError
import com.on.turip.feature.home.impl.model.toUiModel
import io.github.aakira.napier.Napier
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val regionRepository: RegionRepository,
    private val contentRepository: ContentRepository,
    private val articleRepository: ArticleRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {
    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState.Idle)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _uiEffect: Channel<HomeUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<HomeUiEffect> = _uiEffect.receiveAsFlow()

    /**
     * 인기 관광지 조회 결과 원본. 지역 이미지가 나중에 도착해도 다시 붙일 수 있도록 들고 있는다.
     */
    private var popularVisitors: List<DestinationVisitor> = emptyList()

    /**
     * 지역 카테고리명 → 이미지 URL.
     *
     * 인기 관광지 응답에는 이미지가 없어서 지역 카테고리 목록에서 이름으로 찾아 붙인다.
     * 국내/해외를 오가며 목록이 바뀌어도 한 번 본 이미지는 잊지 않도록 덮어쓰지 않고 누적한다.
     */
    private val regionImageUrls: MutableMap<String, String> = mutableMapOf()

    init {
        loadContents()
        loadPopularDestinations()
        loadArticles()
    }

    /**
     * 인기 관광지 CTA 에 쓸 Top 10 을 조회한다.
     *
     * [loadContents] 의 병렬 조회에 넣지 않는다. 거기서는 하나만 실패해도 홈 전체가 에러 화면으로 떨어지는데,
     * CTA 한 줄 때문에 홈이 닫히면 안 된다. 실패하면 목록을 비워 둔 채 버튼은 지도로 그대로 이동한다.
     */
    private fun loadPopularDestinations() {
        viewModelScope.launch {
            regionRepository
                .loadPopularDestinations()
                .onSuccess { popularDestination: PopularDestination ->
                    popularVisitors = popularDestination.destinations
                    publishPopularDestinations()
                    Napier.d("인기 관광지 조회: ${popularDestination.destinations}")
                }.onFailure {
                    Napier.e("인기 관광지 조회 실패")
                }
        }
    }

    private fun rememberRegionImages(regionCategories: List<RegionCategory>) {
        regionCategories.forEach { regionCategory: RegionCategory ->
            regionImageUrls[regionCategory.name] = regionCategory.imageUrl
        }
        publishPopularDestinations()
    }

    private fun publishPopularDestinations() {
        _uiState.update { state: HomeUiState ->
            state.copy(
                popularDestinations =
                    popularVisitors.map { visitor: DestinationVisitor ->
                        visitor.toUiModel(imageUrl = regionImageUrls[visitor.regionCategoryName])
                    },
            )
        }
    }

    /**
     * 인기 북마크와 지역 카테고리를 각각 독립적으로 조회한다.
     *
     * 하나로 묶어 기다리지 않는다. 묶으면 둘 다 도착할 때까지 화면 전체가 비어 있는데,
     * 홈 상단(타이틀·검색·랜덤 여행)은 API 와 무관하므로 바로 보여 주고 먼저 온 섹션부터 채운다.
     * 대신 어느 하나라도 전역 에러(네트워크/서버/토큰 만료)면 홈 전체를 에러 화면으로 바꾸는 정책은 유지한다.
     */
    fun loadContents() {
        loadUsersLikeContents()
        loadRegionCategories()
    }

    private fun loadUsersLikeContents() {
        viewModelScope.launch {
            _uiState.update { it.copy(isUsersLikeLoading = true) }

            contentRepository
                .loadPopularFavoriteContents()
                .onSuccess { usersLikeContents: List<UsersLikeContent> ->
                    _uiState.update { state: HomeUiState ->
                        state.copy(
                            isUsersLikeLoading = false,
                            usersLikeContents = usersLikeContents.map { it.toUiModel() },
                            errorUiState = ErrorUiState.None,
                        )
                    }
                    Napier.d("인기 북마크 목록: $usersLikeContents")
                }.onFailure { errorType: ErrorType ->
                    _uiState.update { it.copy(isUsersLikeLoading = false) }
                    handleGlobalError(errorType.toUiError())
                    Napier.e("인기 북마크 목록 조회 실패: $errorType")
                }
        }
    }

    private fun loadRegionCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRegionsLoading = true) }

            regionRepository
                .loadRegionCategories(uiState.value.isDomesticSelected)
                .onSuccess { regionCategories: List<RegionCategory> ->
                    _uiState.update { state: HomeUiState ->
                        state.copy(
                            isRegionsLoading = false,
                            regionCategories = regionCategories,
                            errorUiState = ErrorUiState.None,
                        )
                    }
                    rememberRegionImages(regionCategories)
                    Napier.d("지역 카테고리 조회: $regionCategories")
                }.onFailure { errorType: ErrorType ->
                    _uiState.update { it.copy(isRegionsLoading = false) }
                    handleGlobalError(errorType.toUiError())
                    Napier.e("지역 카테고리 조회 실패: $errorType")
                }
        }
    }

    /**
     * 랜덤 여행은 지역 목록을 재료로 슬롯을 돌린다.
     * 목록을 확보하지 못한 상태(오프라인 등)에서는 화면을 전환하지 않고 안내만 한다.
     */
    fun clickRandomTravel() {
        viewModelScope.launch {
            if (uiState.value.regionCategories.isEmpty()) {
                _uiEffect.send(HomeUiEffect.ShowRandomTravelUnavailable)
                return@launch
            }
            _uiEffect.send(HomeUiEffect.NavigateToRandomTravel)
        }
    }

    fun updateDomesticSelected(isDomesticSelected: Boolean) {
        Napier.d(if (isDomesticSelected) "국내 클릭" else "해외 클릭")
        viewModelScope.launch {
            _uiState.update { it.copy(isRegionsLoading = true, isDomesticSelected = isDomesticSelected) }

            regionRepository
                .loadRegionCategories(isDomesticSelected)
                .onSuccess { regionCategories: List<RegionCategory> ->
                    _uiState.update { state: HomeUiState ->
                        state.copy(
                            isRegionsLoading = false,
                            regionCategories = regionCategories,
                            errorUiState = ErrorUiState.None,
                        )
                    }
                    rememberRegionImages(regionCategories)
                    Napier.d("지역 카테고리 조회: $regionCategories")
                }.onFailure { errorType: ErrorType ->
                    _uiState.update { it.copy(isRegionsLoading = false) }
                    handleGlobalError(errorType.toUiError())
                    Napier.e("지역 카테고리 조회 실패")
                }
        }
    }

    /**
     * 매거진은 홈의 보조 콘텐츠라 실패해도 홈 전체를 에러 화면으로 바꾸지 않는다.
     * 목록이 비면 섹션 자체가 그려지지 않는다.
     */
    fun loadArticles() {
        viewModelScope.launch {
            _uiState.update { it.copy(isArticlesLoading = true) }

            articleRepository
                .loadArticles(size = HOME_ARTICLES_SIZE)
                .onSuccess { result: ArticlesResult ->
                    _uiState.update { state: HomeUiState ->
                        state.copy(
                            articles = result.articles.map { it.toUiModel() }.toImmutableList(),
                            isArticlesLoading = false,
                        )
                    }
                    Napier.d("매거진 아티클 조회: ${result.articles.size}건")
                }.onFailure { errorType: ErrorType ->
                    _uiState.update { it.copy(isArticlesLoading = false) }
                    Napier.e("매거진 아티클 조회 실패: $errorType")
                }
        }
    }

    private suspend fun handleGlobalError(uiError: UiError) {
        if (uiError is UiError.Global) {
            when (uiError) {
                UiError.Global.Network -> {
                    _uiState.update { it.copy(errorUiState = ErrorUiState.Network) }
                }

                UiError.Global.Server -> {
                    _uiState.update { it.copy(errorUiState = ErrorUiState.Server) }
                }

                UiError.Global.TokenExpired -> {
                    sessionManager.switchToGuest()
                    _uiEffect.send(HomeUiEffect.NavigateToLogin)
                }
            }
        }
    }

    companion object {
        /**
         * 홈 매거진은 가로 스크롤 캐러셀이라 첫 페이지 몇 장이면 충분하다.
         */
        private const val HOME_ARTICLES_SIZE: Int = 5
    }
}
