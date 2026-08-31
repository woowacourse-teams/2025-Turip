package com.on.turip.feature.mypage.impl.notificationsetting.platform

import androidx.compose.runtime.Composable

internal class NotificationPermissionActions(
    val isNotificationsEnabled: suspend () -> Boolean,
    val openNotificationSettings: () -> Unit,
)

@Composable
internal expect fun rememberNotificationPermissionActions(): NotificationPermissionActions
