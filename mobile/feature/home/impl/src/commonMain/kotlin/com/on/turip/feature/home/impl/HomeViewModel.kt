package com.on.turip.feature.home.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.core.data.session.SessionManager
import com.on.turip.core.domain.repository.ContentRepository
import com.on.turip.core.domain.repository.RegionRepository
import com.on.turip.core.model.content.UsersLikeContent
import com.on.turip.core.model.region.PopularDestination
import com.on.turip.core.model.region.RegionCategory
import com.on.turip.core.model.result.ErrorType
import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.model.result.onFailure
import com.on.turip.core.model.result.onSuccess
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.core.ui.error.UiError
import com.on.turip.core.ui.error.toUiError
import io.github.aakira.napier.Napier
import kotlinx.coroutines.async
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
    private val sessionManager: SessionManager,
) : ViewModel() {
    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState.Idle)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _uiEffect: Channel<HomeUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<HomeUiEffect> = _uiEffect.receiveAsFlow()

    init {
        loadContents()
        loadPopularDestinations()
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
                    _uiState.update { state: HomeUiState ->
                        state.copy(
                            popularDestinations = popularDestination.destinations.map { it.toUiModel() },
                        )
                    }
                    Napier.d("인기 관광지 조회: ${popularDestination.destinations}")
                }.onFailure {
                    Napier.e("인기 관광지 조회 실패")
                }
        }
    }

    fun loadContents() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val usersLikeContentsDeferred =
                async { contentRepository.loadPopularFavoriteContents() }
            val regionCategoriesDeferred =
                async { regionRepository.loadRegionCategories(uiState.value.isDomesticSelected) }

            val usersLikeContentsResult = usersLikeContentsDeferred.await()
            val regionCategoriesResult = regionCategoriesDeferred.await()

            val failure: TuripResult.Failure? =
                listOf(usersLikeContentsResult, regionCategoriesResult)
                    .filterIsInstance<TuripResult.Failure>()
                    .firstOrNull()

            if (failure != null) {
                handleGlobalError(failure.errorType.toUiError())
                return@launch
            }

            val usersLikeContents: List<UsersLikeContent> =
                (usersLikeContentsResult as TuripResult.Success).value
            val regionCategories: List<RegionCategory> =
                (regionCategoriesResult as TuripResult.Success).value

            _uiState.update { state: HomeUiState ->
                state.copy(
                    isLoading = false,
                    regionCategories = regionCategories,
                    usersLikeContents = usersLikeContents.map { it.toUiModel() },
                    errorUiState = ErrorUiState.None,
                )
            }

            Napier.d("인기 북마크 목록: $usersLikeContents")
            Napier.d("지역 카테고리 조회: $regionCategories")
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
            regionRepository
                .loadRegionCategories(isDomesticSelected)
                .onSuccess { regionCategories: List<RegionCategory> ->
                    _uiState.update { state: HomeUiState ->
                        state.copy(
                            isLoading = false,
                            regionCategories = regionCategories,
                            isDomesticSelected = isDomesticSelected,
                            errorUiState = ErrorUiState.None,
                        )
                    }
                    Napier.d("지역 카테고리 조회: $regionCategories")
                }.onFailure { errorType: ErrorType ->
                    when (val uiError: UiError = errorType.toUiError()) {
                        is UiError.Global -> handleGlobalError(uiError)
                        is UiError.Feature -> Unit
                    }
                    Napier.e("지역 카테고리 조회 실패")
                }
        }
    }

    private suspend fun handleGlobalError(uiError: UiError) {
        if (uiError is UiError.Global) {
            when (uiError) {
                UiError.Global.Network -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorUiState = ErrorUiState.Network)
                    }
                }

                UiError.Global.Server -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorUiState = ErrorUiState.Server)
                    }
                }

                UiError.Global.TokenExpired -> {
                    sessionManager.switchToGuest()
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEffect.send(HomeUiEffect.NavigateToLogin)
                }
            }
        }
    }
}
