package com.on.turip.ui.main.bookmarks

import com.on.turip.domain.favorite.BookmarkContent
import com.on.turip.ui.common.error.ErrorUiState

data class BookmarkContentUiState(
    val isLoading: Boolean,
    val bookmarkContents: List<BookmarkContent>,
    val errorUiState: ErrorUiState,
) {
    val isEmpty: Boolean
        get() = bookmarkContents.isEmpty() && this != Idle

    companion object {
        val Idle: BookmarkContentUiState =
            BookmarkContentUiState(
                isLoading = true,
                bookmarkContents = emptyList(),
                errorUiState = ErrorUiState.None,
            )
    }
}
