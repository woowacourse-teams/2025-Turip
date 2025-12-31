package com.on.turip.ui.compose.setting.model

import com.on.turip.data.common.ErrorUiState

sealed interface SettingUiEffect {
    data object NavigateToLogin : SettingUiEffect

    data class ShowError(
        val errorUiState: ErrorUiState,
        val onRetryClick: (() -> Unit)? = null,
    ) : SettingUiEffect
}
