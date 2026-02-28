package com.on.turip.ui.compose.bookmark

import androidx.compose.runtime.Immutable
import com.on.turip.domain.bookmark.BookmarkContent
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.paging.PagingState
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class BookmarkContentListUiState(
    val isLoading: Boolean,
    val bookmarkContents: PagingState<BookmarkContent>,
    val totalBookmarkCount: Int?,
    val errorUiState: ErrorUiState,
) {
    val isEmpty: Boolean
        get() = !isLoading && bookmarkContents.items.isEmpty() && errorUiState == ErrorUiState.None

    companion object {
        val Idle: BookmarkContentListUiState =
            BookmarkContentListUiState(
                isLoading = true,
                bookmarkContents =
                    PagingState(
                        items = persistentListOf(),
                        hasNext = false,
                        isAppending = false,
                        errorUiState = ErrorUiState.None,
                    ),
                totalBookmarkCount = null,
                errorUiState = ErrorUiState.None,
            )
    }
}
