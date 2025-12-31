package com.on.turip.ui.compose.setting.model

import com.on.turip.ui.common.error.ErrorUiState

sealed interface SettingUiEffect {
    data object NavigateToLogin : SettingUiEffect

    data class ShowError(
        val errorUiState: ErrorUiState,
        val onRetryClick: (() -> Unit)? = null,
    ) : SettingUiEffect
}
