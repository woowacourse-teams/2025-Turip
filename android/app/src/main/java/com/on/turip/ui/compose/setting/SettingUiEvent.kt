package com.on.turip.ui.compose.setting

import androidx.compose.runtime.Immutable
import com.on.turip.domain.ErrorEvent

@Immutable
sealed interface SettingUiEvent {
    data object Logout : SettingUiEvent

    data object Withdraw : SettingUiEvent

    data class ShowError(
        val errorEvent: ErrorEvent,
    ) : SettingUiEvent
}
