package com.on.turip.feature.bookmark.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.core.data.session.SessionManager
import com.on.turip.core.domain.repository.BookmarkRepository
import com.on.turip.core.model.bookmark.BookmarkContent
import com.on.turip.core.model.paging.Cursor
import com.on.turip.core.model.paging.Page
import com.on.turip.core.model.result.ErrorType
import com.on.turip.core.model.result.onFailure
import com.on.turip.core.model.result.onSuccess
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.core.ui.error.UiError
import com.on.turip.core.ui.error.toUiError
import com.on.turip.feature.bookmark.impl.paging.PagingLoadMode
import io.github.aakira.napier.Napier
import kotlinx.collections.immutable.ImmutableList
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

class BookmarkContentListViewModel(
    private val bookmarkRepository: BookmarkRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {
    private val _uiState: MutableStateFlow<BookmarkContentListUiState> =
        MutableStateFlow(BookmarkContentListUiState.Idle)
    val uiState: StateFlow<BookmarkContentListUiState> = _uiState.asStateFlow()

    private val _uiEffect: Channel<BookmarkContentListUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<BookmarkContentListUiEffect> = _uiEffect.receiveAsFlow()

    init {
        observeBookmarkContents()
        refreshBookmarkContents()
    }

    private fun observeBookmarkContents() {
        viewModelScope.launch {
            bookmarkRepository.bookmarkContents.collect { contents ->
                _uiState.update { state ->
                    state.copy(bookmarkContents = state.bookmarkContents.copy(items = contents))
                }
            }
        }
    }

    fun refreshBookmarkContents() {
        loadBookmarkContents(PagingLoadMode.REFRESH)
    }

    fun loadMoreContents() {
        loadBookmarkContents(PagingLoadMode.APPEND)
    }

    private fun loadBookmarkContents(loadMode: PagingLoadMode) {
        viewModelScope.launch {
            if (!prepareLoadingState(loadMode)) return@launch

            if (loadMode == PagingLoadMode.REFRESH) launch { loadBookmarkCount() }

            val lastBookmarkId: Long? =
                uiState.value.bookmarkContents.items
                    .lastOrNull()
                    ?.bookmarkId

            val cursor = Cursor(size = PAGE_SIZE, lastId = lastBookmarkId)
            bookmarkRepository
                .loadBookmarks(cursor)
                .onSuccess { result: Page<BookmarkContent> ->
                    Napier.d("북마크 화면 조회 성공 mode = $loadMode, cursor = $cursor")
                    applyBookmarkContents(loadMode, result)
                }.onFailure { errorType: ErrorType ->
                    Napier.e("북마크 화면 에러 loadMode = $loadMode")
                    val uiError = errorType.toUiError()
                    if (uiError !is UiError.Global) return@onFailure
                    when (loadMode) {
                        PagingLoadMode.REFRESH -> applyBookmarkContentsRefreshFailure(uiError)
                        PagingLoadMode.APPEND -> applyBookmarkContentsAppendFailure(uiError)
                    }
                }
        }
    }

    private fun prepareLoadingState(loadMode: PagingLoadMode): Boolean =
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
                true
            }

            PagingLoadMode.APPEND -> {
                val pagingState = uiState.value.bookmarkContents
                val canAppend =
                    pagingState.hasNext && pagingState.items.isNotEmpty() && !pagingState.isAppending
                if (!canAppend) {
                    false
                } else {
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
                    true
                }
            }
        }

    private suspend fun loadBookmarkCount() {
        bookmarkRepository
            .loadBookmarkCount()
            .onSuccess { count: Int ->
                _uiState.update { state -> state.copy(totalBookmarkCount = count) }
            }.onFailure {
                _uiState.update { state -> state.copy(totalBookmarkCount = null) }
            }
    }

    private fun applyBookmarkContents(
        loadMode: PagingLoadMode,
        result: Page<BookmarkContent>,
    ) {
        _uiState.update { state ->
            state.copy(
                isLoading = false,
                bookmarkContents =
                    state.bookmarkContents.copy(
                        hasNext = result.hasNext,
                        isAppending = false,
                        errorUiState = ErrorUiState.None,
                    ),
                errorUiState = ErrorUiState.None,
            )
        }
    }

    private suspend fun applyBookmarkContentsRefreshFailure(uiError: UiError.Global) {
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
                _uiEffect.send(BookmarkContentListUiEffect.NavigateToLogin)
            }
        }
    }

    private suspend fun applyBookmarkContentsAppendFailure(uiError: UiError.Global) {
        when (uiError) {
            UiError.Global.Network -> {
                _uiState.update { state ->
                    state.copy(
                        bookmarkContents =
                            state.bookmarkContents.copy(
                                isAppending = false,
                                errorUiState = ErrorUiState.Network,
                            ),
                    )
                }
            }

            UiError.Global.Server -> {
                _uiState.update { state ->
                    state.copy(
                        bookmarkContents =
                            state.bookmarkContents.copy(
                                isAppending = false,
                                errorUiState = ErrorUiState.Server,
                            ),
                    )
                }
            }

            UiError.Global.TokenExpired -> {
                sessionManager.switchToGuest()
                _uiState.update { state ->
                    state.copy(bookmarkContents = state.bookmarkContents.copy(isAppending = false))
                }
                _uiEffect.send(BookmarkContentListUiEffect.NavigateToLogin)
            }
        }
    }

    private val removeBookmarkMutex = Mutex()
    private val removingIds = mutableSetOf<Long>()
    private val removingSnapshots = mutableMapOf<Long, BookmarkRemoveSnapshot>()

    fun removeBookmark(contentId: Long) {
        viewModelScope.launch {
            val acquired = removeBookmarkMutex.withLock { removingIds.add(contentId) }
            if (!acquired) return@launch

            try {
                val removed =
                    removeBookmarkMutex.withLock {
                        val contents = _uiState.value.bookmarkContents.items

                        val removeContentIndex: Int =
                            contents.indexOfFirst { it.content.id == contentId }
                        if (removeContentIndex == -1) return@withLock false

                        val removeContent = contents[removeContentIndex]
                        removingSnapshots[contentId] = BookmarkRemoveSnapshot(removeContent)

                        val updated =
                            contents.filter { it.content.id != contentId }.toImmutableList()
                        _uiState.update { state ->
                            state.copy(bookmarkContents = state.bookmarkContents.copy(items = updated))
                        }

                        true
                    }

                if (!removed) return@launch

                bookmarkRepository
                    .deleteBookmark(contentId)
                    .onSuccess {
                        removeBookmarkMutex.withLock { removingSnapshots.remove(contentId) }
                        _uiState.update { state ->
                            state.copy(totalBookmarkCount = state.totalBookmarkCount?.minus(1))
                        }
                    }.onFailure {
                        _uiEffect.send(
                            BookmarkContentListUiEffect.ShowBookmarkRemoveFailedList(contentId),
                        )
                    }
            } finally {
                removeBookmarkMutex.withLock { removingIds.remove(contentId) }
            }
        }
    }

    fun rollbackBookmarkContentRemove(contentId: Long) {
        viewModelScope.launch {
            removeBookmarkMutex.withLock {
                val snapshot = removingSnapshots.remove(contentId) ?: return@withLock

                val contents = _uiState.value.bookmarkContents.items
                if (contents.any { it.content.id == contentId }) return@withLock

                val rollbackContents: ImmutableList<BookmarkContent> =
                    contents
                        .toMutableList()
                        .apply { add(snapshot.content) }
                        .sortedByDescending { it.bookmarkId }
                        .toImmutableList()

                _uiState.update { state ->
                    state.copy(bookmarkContents = state.bookmarkContents.copy(items = rollbackContents))
                }
            }
        }
    }

    companion object {
        private const val PAGE_SIZE = 7
    }
}

private data class BookmarkRemoveSnapshot(
    val content: BookmarkContent,
)
