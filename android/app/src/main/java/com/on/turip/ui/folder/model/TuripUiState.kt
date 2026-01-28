package com.on.turip.ui.folder.model

import com.on.turip.ui.common.error.ErrorUiState

data class TuripUiState(
    val isLoading: Boolean,
    val errorUiState: ErrorUiState,
    val turips: List<TuripEditModel>,
) {
    val isEmpty: Boolean
        get() = turips.isEmpty() && this != Idle

    companion object {
        val Idle: TuripUiState =
            TuripUiState(
                isLoading = true,
                errorUiState = ErrorUiState.None,
                turips = emptyList(),
            )
    }
}
