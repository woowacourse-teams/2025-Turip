package com.on.turip.ui.compose.folder

import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.main.favorite.model.TuripModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class FolderUiState(
    val isLoading: Boolean,
    val errorUiState: ErrorUiState,
    val turips: ImmutableList<TuripModel>,
) {
    companion object {
        val Idle =
            FolderUiState(
                isLoading = false,
                errorUiState = ErrorUiState.None,
                turips = persistentListOf(),
            )
    }
}
