package com.on.turip.feature.mypage.impl.notificationsetting

import com.on.turip.core.ui.UiIntent

sealed interface NotificationSettingIntent : UiIntent {
    data class ToggleNotification(
        val enabled: Boolean,
    ) : NotificationSettingIntent

    data class UpdateSystemPermission(
        val granted: Boolean,
    ) : NotificationSettingIntent

    data object ClickGoToSettings : NotificationSettingIntent

    data object DismissDialog : NotificationSettingIntent
}
