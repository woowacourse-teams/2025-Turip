package com.on.turip.ui.compose.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.core.result.ErrorType
import com.on.turip.core.result.TuripResult
import com.on.turip.core.result.onFailure
import com.on.turip.core.result.onSuccess
import com.on.turip.domain.content.UsersLikeContent
import com.on.turip.domain.content.repository.ContentRepository
import com.on.turip.domain.region.RegionCategory
import com.on.turip.domain.region.repository.RegionRepository
import com.on.turip.domain.session.SessionManager
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.error.UiError
import com.on.turip.ui.common.error.toUiError
import com.on.turip.ui.common.mapper.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
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

            Timber.d("인기 북마크 목록: $usersLikeContents")
            Timber.d("지역 카테고리 조회: $regionCategories")
        }
    }

    fun updateDomesticSelected(isDomesticSelected: Boolean) {
        Timber.d(if (isDomesticSelected) "국내 클릭" else "해외 클릭")
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
                    Timber.d("지역 카테고리 조회: $regionCategories")
                }.onFailure { errorType: ErrorType ->
                    when (val uiError: UiError = errorType.toUiError()) {
                        is UiError.Global -> handleGlobalError(uiError)
                        is UiError.Feature -> Unit
                    }
                    Timber.e("지역 카테고리 조회 실패")
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
