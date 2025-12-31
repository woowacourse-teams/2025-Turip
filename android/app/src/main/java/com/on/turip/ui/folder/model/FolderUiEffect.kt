package com.on.turip.ui.folder.model

import com.on.turip.ui.common.error.ErrorUiState

sealed interface FolderUiEffect {
    data object NavigateToLogin : FolderUiEffect

    data object FolderAdded : FolderUiEffect

    data object FolderUpdated : FolderUiEffect

    data object FolderDeleted : FolderUiEffect

    class ShowError(
        val errorUiState: ErrorUiState,
        val onRetryClick: () -> Unit
    ) : FolderUiEffect
}
