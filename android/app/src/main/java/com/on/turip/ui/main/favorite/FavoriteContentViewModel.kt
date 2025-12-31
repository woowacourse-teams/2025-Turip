package com.on.turip.ui.main.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.core.result.ErrorType
import com.on.turip.data.result.onFailure
import com.on.turip.data.result.onSuccess
import com.on.turip.domain.favorite.PagedFavoriteContents
import com.on.turip.domain.favorite.repository.FavoriteRepository
import com.on.turip.domain.favorite.usecase.UpdateFavoriteUseCase
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.error.UiError
import com.on.turip.ui.common.error.toUiError
import com.on.turip.ui.main.favorite.model.FavoriteContentUiEffect
import com.on.turip.ui.main.favorite.model.FavoriteContentUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class FavoriteContentViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val updateFavoriteUseCase: UpdateFavoriteUseCase,
) : ViewModel() {
    private val _uiState: MutableStateFlow<FavoriteContentUiState> =
        MutableStateFlow(FavoriteContentUiState.Idle)
    val uiState: StateFlow<FavoriteContentUiState> = _uiState.asStateFlow()

    private val _uiEffect: Channel<FavoriteContentUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<FavoriteContentUiEffect> = _uiEffect.receiveAsFlow()

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
                            errorUiState = ErrorUiState.None,
                        )
                    }
                }.onFailure { errorType: ErrorType ->
                    Timber.e("찜 목록 데이터 조회 에러 발생")
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
                                    it.copy(isLoading = false, errorUiState = ErrorUiState.Server)
                                }
                            }

                            UiError.Global.TokenExpired -> {
                                _uiState.update { it.copy(isLoading = false) }
                                _uiEffect.send(FavoriteContentUiEffect.NavigateToLogin)
                            }
                        }
                    }
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
                    Timber.e("찜 목록에서 찜 버튼 클릭 업데이트 실패")
                    _uiState.update { it.copy(isLoading = false) }
                    val uiError: UiError = errorType.toUiError()
                    if (uiError is UiError.Global) {
                        when (uiError) {
                            UiError.Global.Network -> {
                                _uiEffect.send(
                                    FavoriteContentUiEffect.ShowError(
                                        errorUiState = ErrorUiState.Network,
                                        onRetryClick = { updateFavorite(contentId, isFavorite) },
                                    ),
                                )
                            }

                            UiError.Global.Server -> {
                                _uiEffect.send(
                                    FavoriteContentUiEffect.ShowError(
                                        errorUiState = ErrorUiState.Server,
                                        onRetryClick = { updateFavorite(contentId, isFavorite) },
                                    ),
                                )
                            }

                            UiError.Global.TokenExpired -> {
                                _uiEffect.send(FavoriteContentUiEffect.NavigateToLogin)
                            }
                        }
                    }
                }
        }
    }
}
