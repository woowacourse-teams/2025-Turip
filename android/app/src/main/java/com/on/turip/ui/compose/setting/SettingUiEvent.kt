package com.on.turip.ui.compose.setting

import androidx.compose.runtime.Immutable

@Immutable
sealed interface SettingUiEvent {
    data object Logout : SettingUiEvent

    data object Withdraw : SettingUiEvent
}
