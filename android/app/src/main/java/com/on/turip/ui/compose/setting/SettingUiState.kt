package com.on.turip.ui.compose.setting

import androidx.compose.runtime.Immutable
import com.on.turip.domain.userstorage.TuripDeviceIdentifier
import com.on.turip.ui.common.model.MemberStatus

// TODO : Immutable vs Stable ?
@Immutable
data class SettingUiState(
    val deviceIdentifier: TuripDeviceIdentifier,
    val memberStatus: MemberStatus,
) {
    companion object {
        val EMPTY: SettingUiState =
            SettingUiState(
                deviceIdentifier = TuripDeviceIdentifier.EMPTY,
                memberStatus = MemberStatus.GUEST,
            )
    }
}
