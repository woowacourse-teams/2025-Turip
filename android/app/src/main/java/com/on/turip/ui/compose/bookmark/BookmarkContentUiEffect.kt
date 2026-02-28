package com.on.turip.ui.compose.bookmark

sealed interface BookmarkContentUiEffect {
    data object NavigateToLogin : BookmarkContentUiEffect

    data object ShowBookmarkRemoveFailed : BookmarkContentUiEffect

    data object BookmarkRemoved : BookmarkContentUiEffect
}
