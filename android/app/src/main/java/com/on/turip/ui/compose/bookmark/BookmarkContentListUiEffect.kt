package com.on.turip.ui.compose.bookmark

sealed interface BookmarkContentListUiEffect {
    data object NavigateToLogin : BookmarkContentListUiEffect

    data class ShowBookmarkRemoveFailedList(
        val contentId: Long,
    ) : BookmarkContentListUiEffect

    data object BookmarkRemovedList : BookmarkContentListUiEffect
}
