package com.on.turip.feature.bookmark.impl.viewmodel

import com.on.turip.core.ui.UiIntent

sealed interface BookmarkIntent : UiIntent {
    data object Refresh : BookmarkIntent
    data object LoadMore : BookmarkIntent
    data class RemoveBookmark(val contentId: Long) : BookmarkIntent
    data class RollbackRemove(val contentId: Long) : BookmarkIntent
}
