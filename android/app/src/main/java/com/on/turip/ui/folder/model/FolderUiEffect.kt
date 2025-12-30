package com.on.turip.ui.folder.model

import com.on.turip.data.common.ErrorUiState

sealed interface FolderUiEffect {
    data object NavigateToLogin : FolderUiEffect

    data class ShowErrorSnackbar(
        val errorUiState: ErrorUiState,
        val onRetryClick: (() -> Unit)? = null,
    ) : FolderUiEffect
}
