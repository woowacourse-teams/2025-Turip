package com.on.turip.feature.bookmark.impl.viewmodel

import androidx.lifecycle.viewModelScope
import com.on.turip.core.domain.repository.BookmarkRepository
import com.on.turip.core.domain.session.SessionManager
import com.on.turip.core.model.Content
import com.on.turip.core.model.SessionState
import com.on.turip.core.ui.BaseViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

class BookmarkViewModel(
    private val bookmarkRepository: BookmarkRepository,
    private val sessionManager: SessionManager,
) : BaseViewModel<BookmarkIntent, BookmarkState, BookmarkEffect>(BookmarkState()) {

    private val pendingRemovals: MutableMap<Long, Content> = mutableMapOf()

    companion object {
        private const val PAGE_SIZE = 20
    }

    init {
        fetchBookmarks(refresh = true)
    }

    override fun onIntent(intent: BookmarkIntent) {
        when (intent) {
            is BookmarkIntent.Refresh -> fetchBookmarks(refresh = true)
            is BookmarkIntent.LoadMore -> fetchBookmarks(refresh = false)
            is BookmarkIntent.RemoveBookmark -> removeBookmark(intent.contentId)
            is BookmarkIntent.RollbackRemove -> rollbackRemove(intent.contentId)
        }
    }

    private fun fetchBookmarks(refresh: Boolean) {
        if (refresh) {
            if (currentState.isLoading) return
            updateState { copy(isLoading = true, isError = false) }
        } else {
            if (currentState.isLoadingMore || !currentState.hasMore) return
            updateState { copy(isLoadingMore = true) }
        }

        viewModelScope.launch {
            if (sessionManager.state.value !is SessionState.LoggedIn) {
                updateState { copy(isLoading = false, isLoadingMore = false) }
                emitEffect(BookmarkEffect.NavigateToLogin)
                return@launch
            }

            val lastId = if (refresh) 0L else currentState.contents.lastOrNull()?.id ?: 0L

            bookmarkRepository.getBookmarks(size = PAGE_SIZE, lastId = lastId)
                .onSuccess { result ->
                    val visibleResult = result.filter { it.id !in pendingRemovals }
                    if (refresh) {
                        updateState {
                            copy(
                                contents = visibleResult.toImmutableList(),
                                isLoading = false,
                                hasMore = result.size >= PAGE_SIZE,
                            )
                        }
                    } else {
                        updateState {
                            copy(
                                contents = (contents + visibleResult).toImmutableList(),
                                isLoadingMore = false,
                                hasMore = result.size >= PAGE_SIZE,
                            )
                        }
                    }
                }
                .onFailure {
                    updateState {
                        copy(
                            isLoading = false,
                            isLoadingMore = false,
                            isError = refresh,
                        )
                    }
                }
        }
    }

    private fun removeBookmark(contentId: Long) {
        val content = currentState.contents.find { it.id == contentId } ?: return
        pendingRemovals[contentId] = content
        updateState {
            copy(contents = contents.filter { it.id != contentId }.toImmutableList())
        }

        viewModelScope.launch {
            bookmarkRepository.removeBookmark(contentId)
                .onSuccess {
                    pendingRemovals.remove(contentId)
                }
                .onFailure {
                    pendingRemovals.remove(contentId)
                    emitEffect(BookmarkEffect.ShowRemoveFailedSnackbar(contentId))
                }
        }
    }

    private fun rollbackRemove(contentId: Long) {
        val content = pendingRemovals.remove(contentId) ?: return
        val restoredList = (currentState.contents + content)
            .sortedByDescending { it.id }
            .toImmutableList()
        updateState { copy(contents = restoredList) }
    }
}
