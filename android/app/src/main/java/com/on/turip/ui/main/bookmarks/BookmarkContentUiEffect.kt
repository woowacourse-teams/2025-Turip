package com.on.turip.ui.main.bookmarks

import com.on.turip.ui.common.error.ErrorUiState

sealed interface BookmarkContentUiEffect {
    data object NavigateToLogin : BookmarkContentUiEffect

    data class ShowError(
        val errorUiState: ErrorUiState,
        val action: BookmarkContentRetryAction,
    ) : BookmarkContentUiEffect
}

sealed interface BookmarkContentRetryAction {
    data class UpdateBookmark(
        val contentId: Long,
        val isBookmarked: Boolean,
    ) : BookmarkContentRetryAction
}
