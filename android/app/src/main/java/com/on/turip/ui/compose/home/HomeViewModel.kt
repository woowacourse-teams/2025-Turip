package com.on.turip.ui.compose.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.data.common.ErrorType
import com.on.turip.data.common.ErrorUiState
import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.common.UiError
import com.on.turip.data.common.onFailure
import com.on.turip.data.common.onFailureWithCause
import com.on.turip.data.common.onSuccess
import com.on.turip.data.common.toUiError
import com.on.turip.domain.content.UsersLikeContent
import com.on.turip.domain.content.repository.ContentRepository
import com.on.turip.domain.region.RegionCategory
import com.on.turip.domain.region.repository.RegionRepository
import com.on.turip.ui.common.event.CommonUiEffect
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
) : ViewModel() {
    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState.Idle)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _commonUiEffect: Channel<CommonUiEffect> = Channel(Channel.BUFFERED)
    val commonUiEffect: Flow<CommonUiEffect> = _commonUiEffect.receiveAsFlow()

    init {
        loadContents()
    }

    fun loadContents() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorUiState = ErrorUiState.None) }
            val usersLikeContentsDeferred =
                async { contentRepository.loadPopularFavoriteContents() }
            val regionCategoriesDeferred =
                async { regionRepository.loadRegionCategories(uiState.value.isDomesticSelected) }

            val usersLikeContentsResult = usersLikeContentsDeferred.await()
            val regionCategoriesResult = regionCategoriesDeferred.await()

            val failure: TuripCustomResult.Failure? =
                listOf(usersLikeContentsResult, regionCategoriesResult)
                    .filterIsInstance<TuripCustomResult.Failure>()
                    .firstOrNull()

            if (failure != null) {
                handleGlobalError(failure.errorType.toUiError())
                return@launch
            }

            val usersLikeContents: List<UsersLikeContent> =
                (usersLikeContentsResult as TuripCustomResult.Success).value
            val regionCategories: List<RegionCategory> =
                (regionCategoriesResult as TuripCustomResult.Success).value

            _uiState.update { state: HomeUiState ->
                state.copy(
                    isLoading = false,
                    regionCategories = regionCategories,
                    usersLikeContents = usersLikeContents.map { it.toUiModel() },
                    errorUiState = ErrorUiState.None,
                )
            }

            Timber.d("인기 찜 목록: $usersLikeContents")
            Timber.d("지역 카테고리 조회: $regionCategories")
        }
    }

    fun updateDomesticSelected(isDomesticSelected: Boolean) {
        _uiState.update { it.copy(isDomesticSelected = isDomesticSelected) }
        Timber.d(if (isDomesticSelected) "국내 클릭" else "해외 클릭")
        loadRegionCategories(isDomesticSelected)
    }

    private fun loadRegionCategories(isDomestic: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorUiState = ErrorUiState.None) }

            regionRepository
                .loadRegionCategories(isDomestic)
                .onSuccess { regionCategories: List<RegionCategory> ->
                    _uiState.update { state: HomeUiState ->
                        state.copy(
                            isLoading = false,
                            regionCategories = regionCategories,
                            isDomesticSelected = isDomestic,
                            errorUiState = ErrorUiState.None,
                        )
                    }
                    Timber.d("지역 카테고리 조회: $regionCategories")
                }.onFailure { errorType: ErrorType ->
                    when (val uiError: UiError = errorType.toUiError()) {
                        is UiError.Global -> handleGlobalError(uiError)
                        is UiError.Feature -> Unit
                    }
                }.onFailureWithCause { errorType: ErrorType, cause: Throwable? ->
                    Timber.e("지역 카테고리 조회 실패 : $errorType / $cause")
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
                    _commonUiEffect.send(CommonUiEffect.NavigateToLogin)
                }
            }
        }
    }
}
