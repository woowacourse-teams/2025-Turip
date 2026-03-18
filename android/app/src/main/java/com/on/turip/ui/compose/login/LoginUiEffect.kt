package com.on.turip.ui.compose.login

import com.on.turip.ui.common.error.ErrorUiState

sealed interface LoginUiEffect {
    data object RequestAutoLogin : LoginUiEffect

    data class NavigateToMain(
        val deepLinkUrl: String? = null,
    ) : LoginUiEffect

    data class ShowError(
        val errorUiState: ErrorUiState,
    ) : LoginUiEffect
}
