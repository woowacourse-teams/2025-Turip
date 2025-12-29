package com.on.turip.ui.main.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.data.common.ErrorType
import com.on.turip.data.common.ErrorUiEffect
import com.on.turip.data.common.ErrorUiState
import com.on.turip.data.common.UiError
import com.on.turip.data.common.onFailure
import com.on.turip.data.common.onFailureWithCause
import com.on.turip.data.common.onSuccess
import com.on.turip.data.common.toUiError
import com.on.turip.domain.favorite.PagedFavoriteContents
import com.on.turip.domain.favorite.repository.FavoriteRepository
import com.on.turip.domain.favorite.usecase.UpdateFavoriteUseCase
import com.on.turip.ui.common.event.CommonUiEffect
import com.on.turip.ui.main.favorite.model.FavoriteContentUiState
import dagger.hilt.android.lifecycle.HiltViewModel
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
class FavoriteContentViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val updateFavoriteUseCase: UpdateFavoriteUseCase,
) : ViewModel() {
    private val _uiState: MutableStateFlow<FavoriteContentUiState> =
        MutableStateFlow(FavoriteContentUiState.Idle)
    val uiState: StateFlow<FavoriteContentUiState> = _uiState.asStateFlow()

    private val _commonUiEffect: Channel<CommonUiEffect> = Channel(Channel.BUFFERED)
    val commonUiEffect: Flow<CommonUiEffect> = _commonUiEffect.receiveAsFlow()

    private val _errorUiEffect: Channel<ErrorUiEffect> = Channel(Channel.BUFFERED)
    val errorUiEffect: Flow<ErrorUiEffect> = _errorUiEffect.receiveAsFlow()

    init {
        loadFavoriteContents()
    }

    fun loadFavoriteContents() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorUiState = ErrorUiState.None) }

            favoriteRepository
                .loadFavoriteContents(10, 0L)
                .onSuccess { pagedFavoriteContent: PagedFavoriteContents ->
                    Timber.d("찜 목록 데이터 조회 성공")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            favoriteContents = pagedFavoriteContent.favoriteContents,
                        )
                    }
                }.onFailure { errorType: ErrorType ->
                    val uiError: UiError = errorType.toUiError()
                    if (uiError is UiError.Global) {
                        when (uiError) {
                            UiError.Global.Network -> {
                                _uiState.update {
                                    it.copy(isLoading = false, errorUiState = ErrorUiState.Network)
                                }
                            }

                            UiError.Global.Server -> {
                                _uiState.update {
                                    it.copy(isLoading = false, errorUiState = ErrorUiState.Network)
                                }
                            }

                            UiError.Global.TokenExpired -> {
                                _commonUiEffect.send(CommonUiEffect.NavigateToLogin)
                            }
                        }
                    }
                }.onFailureWithCause { errorType: ErrorType, cause: Throwable? ->
                    Timber.e("찜 목록 데이터 조회 에러 발생 : $errorType / $cause")
                }
        }
    }

    fun updateFavorite(
        contentId: Long,
        isFavorite: Boolean,
    ) {
        val updatedFavorite: Boolean = !isFavorite

        viewModelScope.launch {
            updateFavoriteUseCase(updatedFavorite, contentId)
                .onSuccess {
                    Timber.d("찜 목록 페이지, 찜 버튼 클릭(contentId=$contentId, updateFavorite = $updatedFavorite")

                    _uiState.update { state: FavoriteContentUiState ->
                        state.copy(
                            isLoading = false,
                            favoriteContents = uiState.value.favoriteContents.filter { it.content.id != contentId },
                            errorUiState = ErrorUiState.None,
                        )
                    }
                }.onFailure { errorType: ErrorType ->
                    val uiError: UiError = errorType.toUiError()
                    if (uiError is UiError.Global) {
                        when (uiError) {
                            UiError.Global.Network -> {
                                _errorUiEffect.send(
                                    ErrorUiEffect.ShowSnackbar(
                                        errorUiState = ErrorUiState.Network,
                                        onRetryClick = { updateFavorite(contentId, isFavorite) },
                                    ),
                                )
                            }

                            UiError.Global.Server -> {
                                _errorUiEffect.send(
                                    ErrorUiEffect.ShowSnackbar(
                                        errorUiState = ErrorUiState.Server,
                                        onRetryClick = null,
                                    ),
                                )
                            }

                            UiError.Global.TokenExpired -> {
                                _commonUiEffect.send(CommonUiEffect.NavigateToLogin)
                            }
                        }
                    }
                }.onFailureWithCause { errorType: ErrorType, cause: Throwable? ->
                    Timber.e("찜 목록에서 찜 버튼 클릭 업데이트 실패 : $errorType / $cause")
                }
        }
    }
}
