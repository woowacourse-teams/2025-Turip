package com.on.turip.feature.mypage.impl.notificationsetting.platform

import android.content.Intent
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat

@Composable
internal actual fun rememberNotificationPermissionActions(): NotificationPermissionActions {
    val context = LocalContext.current
    return remember(context) {
        NotificationPermissionActions(
            isNotificationsEnabled = { NotificationManagerCompat.from(context).areNotificationsEnabled() },
            openNotificationSettings = {
                val intent =
                    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    }
                context.startActivity(intent)
            },
        )
    }
}
