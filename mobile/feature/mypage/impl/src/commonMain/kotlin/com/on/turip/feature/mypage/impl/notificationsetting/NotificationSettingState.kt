package com.on.turip.feature.mypage.impl.notificationsetting

import androidx.compose.runtime.Immutable
import com.on.turip.core.ui.UiState

@Immutable
data class NotificationSettingState(
    val isPushNotificationEnabled: Boolean = true,
    val isSystemNotificationEnabled: Boolean = true,
    val isLoading: Boolean = true,
    val dialogState: NotificationSettingDialogState? = null,
) : UiState {
    val shouldShowSystemNotificationBanner: Boolean =
        isPushNotificationEnabled && !isSystemNotificationEnabled
}

sealed interface NotificationSettingDialogState {
    data object SystemNotificationDisabled : NotificationSettingDialogState
}
