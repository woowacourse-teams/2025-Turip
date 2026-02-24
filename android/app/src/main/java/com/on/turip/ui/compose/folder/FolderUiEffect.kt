package com.on.turip.ui.compose.folder

import com.on.turip.ui.common.error.ErrorUiState

sealed interface FolderUiEffect {
    data object NavigateToLogin : FolderUiEffect

    data class TuripAdded(
        val turipName: String,
    ) : FolderUiEffect

    data class TuripDeleted(
        val turipName: String,
    ) : FolderUiEffect

    data class ShowError(
        val errorUiState: ErrorUiState,
        val retryAction: FolderRetryAction,
    ) : FolderUiEffect
}

sealed interface FolderRetryAction {
    data object UpdateFolder : FolderRetryAction

    data class AddFolder(
        val name: String,
    ) : FolderRetryAction
}
