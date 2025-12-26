package com.on.turip.ui.compose.setting.model

import androidx.compose.runtime.Immutable
import com.on.turip.data.common.UiError

@Immutable
sealed interface SettingUiEffect {
    data object NavigateToLogin : SettingUiEffect

    data class ShowError(
        val uiError: UiError,
    ) : SettingUiEffect
}
