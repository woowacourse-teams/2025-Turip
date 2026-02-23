package com.on.turip.ui.compose.bookmarks

sealed interface BookmarkContentUiEffect {
    data object NavigateToLogin : BookmarkContentUiEffect

    data object ShowBookmarkRemoveFailed : BookmarkContentUiEffect

    data object BookmarkRemoved : BookmarkContentUiEffect
}
