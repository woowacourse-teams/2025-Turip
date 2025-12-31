package com.on.turip.ui.folder.model

import com.on.turip.ui.common.error.ErrorUiState

data class FolderUiState(
    val isLoading: Boolean,
    val errorUiState: ErrorUiState,
    val folders: List<FolderEditModel>,
) {
    val isEmpty: Boolean
        get() = folders.isEmpty()

    companion object {
        val Idle: FolderUiState =
            FolderUiState(
                isLoading = false,
                errorUiState = ErrorUiState.None,
                folders = emptyList(),
            )
    }
}
