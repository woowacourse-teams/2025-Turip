package com.on.turip.feature.bookmark.impl.viewmodel

import com.on.turip.core.ui.UiEffect

sealed interface BookmarkEffect : UiEffect {
    data object NavigateToLogin : BookmarkEffect
    data class ShowRemoveFailedSnackbar(val contentId: Long) : BookmarkEffect
}
