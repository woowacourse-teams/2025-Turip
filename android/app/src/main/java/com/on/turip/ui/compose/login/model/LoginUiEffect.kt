package com.on.turip.ui.compose.login.model

import com.on.turip.ui.common.error.ErrorUiState

sealed interface LoginUiEffect {
    data object NavigateToMain : LoginUiEffect

    data class ShowError(
        val errorUiState: ErrorUiState,
    ) : LoginUiEffect
}
