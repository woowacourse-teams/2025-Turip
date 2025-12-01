package com.on.turip.ui.compose.setting

import androidx.compose.runtime.Immutable
import com.on.turip.domain.userstorage.TuripDeviceIdentifier
import com.on.turip.ui.common.model.MemberStatus

@Immutable
data class SettingUiState(
    val deviceIdentifier: TuripDeviceIdentifier,
    val memberStatus: MemberStatus,
    val showLogoutDialog: Boolean,
    val showWithdrawDialog: Boolean,
    val isNetworkError: Boolean,
    val isServerError: Boolean,
) {
    companion object {
        val EMPTY: SettingUiState =
            SettingUiState(
                deviceIdentifier = TuripDeviceIdentifier.EMPTY,
                memberStatus = MemberStatus.MEMBER,
                showLogoutDialog = false,
                showWithdrawDialog = false,
                isNetworkError = false,
                isServerError = false,
            )
    }
}
