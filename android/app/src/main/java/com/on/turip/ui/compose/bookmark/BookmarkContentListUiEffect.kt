package com.on.turip.ui.compose.bookmark

sealed interface BookmarkContentListUiEffect {
    data object NavigateToLogin : BookmarkContentListUiEffect

    data object ShowBookmarkRemoveFailedList : BookmarkContentListUiEffect

    data object BookmarkRemovedList : BookmarkContentListUiEffect
}
