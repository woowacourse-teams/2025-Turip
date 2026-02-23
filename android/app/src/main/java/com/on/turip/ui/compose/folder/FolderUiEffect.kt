package com.on.turip.ui.compose.folder

import com.on.turip.ui.common.error.ErrorUiState

sealed interface FolderUiEffect {
    data object NavigateToLogin : FolderUiEffect

    data object TuripAdded : FolderUiEffect

    data object TuripDeleted : FolderUiEffect

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
