package com.on.turip.ui.compose.bookmarks

import androidx.compose.runtime.Immutable
import com.on.turip.domain.bookmark.BookmarkContent
import com.on.turip.ui.common.error.ErrorUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class BookmarkContentUiState(
    val isLoading: Boolean,
    val bookmarkContents: ImmutableList<BookmarkContent>,
    val errorUiState: ErrorUiState,
) {
    val isEmpty: Boolean
        get() = bookmarkContents.isEmpty() && this != Idle

    companion object {
        val Idle: BookmarkContentUiState =
            BookmarkContentUiState(
                isLoading = true,
                bookmarkContents = persistentListOf(),
                errorUiState = ErrorUiState.None,
            )
    }
}
