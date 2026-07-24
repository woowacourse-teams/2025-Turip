package com.on.turip.feature.login.impl

import com.on.turip.core.ui.error.ErrorUiState

sealed interface LoginUiEffect {
    data object RequestAutoLogin : LoginUiEffect

    data class NavigateToMain(
        val deepLinkUrl: String? = null,
    ) : LoginUiEffect

    data class ShowError(
        val errorUiState: ErrorUiState,
    ) : LoginUiEffect
}
