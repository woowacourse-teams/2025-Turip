package com.on.turip.feature.bookmark.impl

sealed interface BookmarkContentListUiEffect {
    data object NavigateToLogin : BookmarkContentListUiEffect

    data class ShowBookmarkRemoveFailedList(
        val contentId: Long,
    ) : BookmarkContentListUiEffect
}
