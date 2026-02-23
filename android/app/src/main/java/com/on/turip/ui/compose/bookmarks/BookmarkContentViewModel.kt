package com.on.turip.ui.compose.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.core.result.ErrorType
import com.on.turip.core.result.onFailure
import com.on.turip.core.result.onSuccess
import com.on.turip.domain.bookmark.BookmarkContent
import com.on.turip.domain.bookmark.repository.BookmarkRepository
import com.on.turip.domain.common.paging.Page
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.error.UiError
import com.on.turip.ui.common.error.toUiError
import com.on.turip.ui.common.paging.PagingLoadMode
import com.on.turip.ui.common.paging.PagingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BookmarkContentViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<BookmarkContentUiState> =
        MutableStateFlow(BookmarkContentUiState.Idle)
    val uiState: StateFlow<BookmarkContentUiState> = _uiState.asStateFlow()

    private val _uiEffect: Channel<BookmarkContentUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<BookmarkContentUiEffect> = _uiEffect.receiveAsFlow()

    init {
        refreshBookmarkContents()
    }

    fun refreshBookmarkContents() {
        loadBookmarkContents(PagingLoadMode.REFRESH)
    }

    fun loadMoreContents() {
        loadBookmarkContents(PagingLoadMode.APPEND)
    }

    private fun loadBookmarkContents(loadMode: PagingLoadMode) {
        viewModelScope.launch {
            when (loadMode) {
                PagingLoadMode.REFRESH -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = true,
                            errorUiState = ErrorUiState.None,
                            bookmarkContents =
                                state.bookmarkContents.copy(
                                    isAppending = false,
                                    errorUiState = ErrorUiState.None,
                                ),
                        )
                    }
                }

                PagingLoadMode.APPEND -> {
                    val pagingState = uiState.value.bookmarkContents
                    if (!pagingState.hasNext || pagingState.items.isEmpty() || pagingState.isAppending) return@launch

                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            bookmarkContents =
                                state.bookmarkContents.copy(
                                    isAppending = true,
                                    errorUiState = ErrorUiState.None,
                                ),
                            errorUiState = ErrorUiState.None,
                        )
                    }
                }
            }

            val lastItemId =
                when (loadMode) {
                    PagingLoadMode.REFRESH -> {
                        0L
                    }

                    PagingLoadMode.APPEND -> {
                        uiState.value.bookmarkContents.items
                            .last()
                            .content.id
                    }
                }

            bookmarkRepository
                .loadBookmarks(PAGE_SIZE, lastItemId)
                .onSuccess { result: Page<BookmarkContent> ->
                    Timber.d("북마크 화면 조회 성공 mode =$loadMode")
                    _uiState.update { state ->
                        val newItems =
                            when (loadMode) {
                                PagingLoadMode.REFRESH -> result.items.toImmutableList()
                                PagingLoadMode.APPEND -> (state.bookmarkContents.items + result.items).toImmutableList()
                            }
                        state.copy(
                            isLoading = false,
                            bookmarkContents =
                                PagingState(
                                    items = newItems,
                                    hasNext = result.hasNext,
                                    isAppending = false,
                                    errorUiState = ErrorUiState.None,
                                ),
                            errorUiState = ErrorUiState.None,
                        )
                    }
                }.onFailure { errorType: ErrorType ->
                    Timber.e("북마크 화면 에러 loadMode = $loadMode")
                    when (loadMode) {
                        PagingLoadMode.REFRESH -> {
                            val uiError: UiError = errorType.toUiError()
                            if (uiError is UiError.Global) {
                                when (uiError) {
                                    UiError.Global.Network -> {
                                        _uiState.update {
                                            it.copy(
                                                isLoading = false,
                                                errorUiState = ErrorUiState.Network,
                                            )
                                        }
                                    }

                                    UiError.Global.Server -> {
                                        _uiState.update {
                                            it.copy(
                                                isLoading = false,
                                                errorUiState = ErrorUiState.Server,
                                            )
                                        }
                                    }

                                    UiError.Global.TokenExpired -> {
                                        _uiState.update { it.copy(isLoading = false) }
                                        _uiEffect.send(BookmarkContentUiEffect.NavigateToLogin)
                                    }
                                }
                            }
                        }

                        PagingLoadMode.APPEND -> {
                            val uiError: UiError = errorType.toUiError()
                            if (uiError is UiError.Global) {
                                when (uiError) {
                                    UiError.Global.Network -> {
                                        _uiState.update {
                                            it.copy(
                                                bookmarkContents =
                                                    it.bookmarkContents.copy(
                                                        isAppending = false,
                                                        errorUiState = ErrorUiState.Network,
                                                    ),
                                            )
                                        }
                                    }

                                    UiError.Global.Server -> {
                                        _uiState.update {
                                            it.copy(
                                                bookmarkContents =
                                                    it.bookmarkContents.copy(
                                                        isAppending = false,
                                                        errorUiState = ErrorUiState.Server,
                                                    ),
                                            )
                                        }
                                    }

                                    UiError.Global.TokenExpired -> {
                                        _uiState.update {
                                            it.copy(
                                                bookmarkContents =
                                                    it.bookmarkContents.copy(isAppending = false),
                                            )
                                        }
                                        _uiEffect.send(BookmarkContentUiEffect.NavigateToLogin)
                                    }
                                }
                            }
                        }
                    }
                }
        }
    }

    private val removeBookmarkMutex = Mutex()

    // 삭제 진행 중인 콘텐츠에 대해 중복 API 호출 방지용
    private val removingIds = mutableSetOf<Long>()

    // 낙관적 UI
    fun removeBookmark(contentId: Long) {
        viewModelScope.launch {
            val acquired = removeBookmarkMutex.withLock { removingIds.add(contentId) }
            // 이미 삭제 진행 중이라면 반환
            if (!acquired) return@launch

            try {
                val removed =
                    removeBookmarkMutex.withLock {
                        val contents = _uiState.value.bookmarkContents

                        // 이미 UI 제거 완료된 상태 (API 호출 완료)
                        if (contents.items.none { it.content.id == contentId }) return@withLock false

                        val updated =
                            contents.items.filter { it.content.id != contentId }.toImmutableList()

                        _uiState.update { state ->
                            state.copy(bookmarkContents = state.bookmarkContents.copy(items = updated))
                        }

                        true
                    }

                if (!removed) return@launch

                bookmarkRepository
                    .deleteBookmark(contentId)
                    .onSuccess {
                        _uiEffect.send(BookmarkContentUiEffect.BookmarkRemoved)
                    }.onFailure {
                        _uiEffect.send(BookmarkContentUiEffect.ShowBookmarkRemoveFailed)
                    }
            } finally {
                // 중복 API 호출 방지 리소스 정리
                removeBookmarkMutex.withLock { removingIds.remove(contentId) }
            }
        }
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}
