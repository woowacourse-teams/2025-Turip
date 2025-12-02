package com.on.turip.ui.compose.setting

import androidx.compose.runtime.Immutable
import com.on.turip.domain.userstorage.TuripDeviceIdentifier

@Immutable
data class SettingUiState(
    val deviceIdentifier: TuripDeviceIdentifier,
    val showLogoutDialog: Boolean,
    val showWithdrawDialog: Boolean,
    val isNetworkError: Boolean,
    val isServerError: Boolean,
) {
    companion object {
        val EMPTY: SettingUiState =
            SettingUiState(
                deviceIdentifier = TuripDeviceIdentifier.EMPTY,
                showLogoutDialog = false,
                showWithdrawDialog = false,
                isNetworkError = false,
                isServerError = false,
            )
    }
}
