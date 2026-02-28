package com.on.turip.ui.compose.bookmark

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
class BookmarkContentListViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<BookmarkContentListUiState> =
        MutableStateFlow(BookmarkContentListUiState.Idle)
    val uiState: StateFlow<BookmarkContentListUiState> = _uiState.asStateFlow()

    private val _uiEffect: Channel<BookmarkContentListUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<BookmarkContentListUiEffect> = _uiEffect.receiveAsFlow()

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
            if (!prepareLoadingState(loadMode)) return@launch

            // 새로고침할 때만 전체 콘텐츠 수 API 호출
            if (loadMode == PagingLoadMode.REFRESH) launch { loadBookmarkCount() }

            val lastItemId: Long = getLastItemId(loadMode) ?: return@launch

            bookmarkRepository
                .loadBookmarks(PAGE_SIZE, lastItemId)
                .onSuccess { result: Page<BookmarkContent> ->
                    Timber.d("북마크 화면 조회 성공 mode =$loadMode")
                    applyBookmarkContents(loadMode, result)
                }.onFailure { errorType: ErrorType ->
                    Timber.e("북마크 화면 에러 loadMode = $loadMode")
                    val uiError: UiError.Global = errorType.toUiError() as UiError.Global
                    when (loadMode) {
                        PagingLoadMode.REFRESH -> applyBookmarkContentsRefreshFailure(uiError)
                        PagingLoadMode.APPEND -> applyBookmarkContentsAppendFailure(uiError)
                    }
                }
        }
    }

    private fun prepareLoadingState(loadMode: PagingLoadMode): Boolean {
        return when (loadMode) {
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
                if (!canAppend) return false

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

    private fun getLastItemId(loadMode: PagingLoadMode) =
        when (loadMode) {
            PagingLoadMode.REFRESH -> {
                0L
            }

            PagingLoadMode.APPEND -> {
                uiState.value.bookmarkContents.items
                    .lastOrNull()
                    ?.content
                    ?.id
            }
        }

    private fun applyBookmarkContents(
        loadMode: PagingLoadMode,
        result: Page<BookmarkContent>,
    ) {
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
    }

    private suspend fun applyBookmarkContentsRefreshFailure(uiError: UiError.Global) {
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
                _uiState.update { state ->
                    state.copy(bookmarkContents = state.bookmarkContents.copy(isAppending = false))
                }
                _uiEffect.send(BookmarkContentListUiEffect.NavigateToLogin)
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
                        _uiState.update { state ->
                            state.copy(totalBookmarkCount = state.totalBookmarkCount?.minus(1))
                        }
                        _uiEffect.send(BookmarkContentListUiEffect.BookmarkRemovedList)
                    }.onFailure {
                        _uiEffect.send(BookmarkContentListUiEffect.ShowBookmarkRemoveFailedList)
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
