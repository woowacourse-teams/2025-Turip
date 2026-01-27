package com.on.turip.ui.main.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.core.result.ErrorType
import com.on.turip.core.result.onFailure
import com.on.turip.core.result.onSuccess
import com.on.turip.domain.favorite.PagedBookmarkContents
import com.on.turip.domain.favorite.repository.BookmarkRepository
import com.on.turip.domain.favorite.usecase.UpdateBookmarkUseCase
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.error.UiError
import com.on.turip.ui.common.error.toUiError
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
class BookmarkContentViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository,
    private val updateBookmarkUseCase: UpdateBookmarkUseCase,
) : ViewModel() {
    private val _uiState: MutableStateFlow<BookmarkContentUiState> =
        MutableStateFlow(BookmarkContentUiState.Idle)
    val uiState: StateFlow<BookmarkContentUiState> = _uiState.asStateFlow()

    private val _uiEffect: Channel<BookmarkContentUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<BookmarkContentUiEffect> = _uiEffect.receiveAsFlow()

    init {
        loadBookmarkContents()
    }

    fun loadBookmarkContents() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            bookmarkRepository
                .loadBookmarks(10, 0L)
                .onSuccess { result: PagedBookmarkContents ->
                    Timber.d("북마크 목록 조회 성공")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            bookmarkContents = result.bookmarkContents,
                            errorUiState = ErrorUiState.None,
                        )
                    }
                }.onFailure { errorType: ErrorType ->
                    Timber.e("북마크 목록 조회 에러 발생")
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
                                _uiEffect.send(BookmarkContentUiEffect.NavigateToLogin)
                            }
                        }
                    }
                }
        }
    }

    fun updateBookmark(
        contentId: Long,
        isBookmarked: Boolean,
    ) {
        val updatedBookmark: Boolean = !isBookmarked

        viewModelScope.launch {
            updateBookmarkUseCase(updatedBookmark, contentId)
                .onSuccess {
                    Timber.d("북마크 목록 화면, 북마크 클릭(contentId=$contentId, updatedBookmark = $updatedBookmark")

                    _uiState.update { state: BookmarkContentUiState ->
                        state.copy(
                            isLoading = false,
                            bookmarkContents = state.bookmarkContents.filter { it.content.id != contentId },
                            errorUiState = ErrorUiState.None,
                        )
                    }
                }.onFailure { errorType: ErrorType ->
                    Timber.e("북마크 목록 화면, 북마크 클릭 실패(contentId=$contentId, originBookmark = $isBookmarked)")
                    _uiState.update { it.copy(isLoading = false) }
                    val uiError: UiError = errorType.toUiError()
                    if (uiError is UiError.Global) {
                        when (uiError) {
                            UiError.Global.Network -> {
                                _uiEffect.send(
                                    BookmarkContentUiEffect.ShowError(
                                        errorUiState = ErrorUiState.Network,
                                        action =
                                            BookmarkContentRetryAction.UpdateBookmark(
                                                contentId = contentId,
                                                isBookmarked = isBookmarked,
                                            ),
                                    ),
                                )
                            }

                            UiError.Global.Server -> {
                                _uiEffect.send(
                                    BookmarkContentUiEffect.ShowError(
                                        errorUiState = ErrorUiState.Server,
                                        action =
                                            BookmarkContentRetryAction.UpdateBookmark(
                                                contentId = contentId,
                                                isBookmarked = isBookmarked,
                                            ),
                                    ),
                                )
                            }

                            UiError.Global.TokenExpired -> {
                                _uiEffect.send(BookmarkContentUiEffect.NavigateToLogin)
                            }
                        }
                    }
                }
        }
    }

    fun handleErrorRetryRequest(action: BookmarkContentRetryAction) {
        when (action) {
            is BookmarkContentRetryAction.UpdateBookmark -> {
                updateBookmark(action.contentId, action.isBookmarked)
            }
        }
    }
}
