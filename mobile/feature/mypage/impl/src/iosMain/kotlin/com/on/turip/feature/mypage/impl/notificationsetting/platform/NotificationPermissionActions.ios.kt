package com.on.turip.feature.mypage.impl.notificationsetting.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.CompletableDeferred
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNUserNotificationCenter

@Composable
internal actual fun rememberNotificationPermissionActions(): NotificationPermissionActions =
    remember {
        NotificationPermissionActions(
            isNotificationsEnabled = {
                val result = CompletableDeferred<Boolean>()
                UNUserNotificationCenter.currentNotificationCenter().getNotificationSettingsWithCompletionHandler { settings ->
                    result.complete(settings?.authorizationStatus == UNAuthorizationStatusAuthorized)
                }
                result.await()
            },
            openNotificationSettings = {
                val url = NSURL.URLWithString(UIApplicationOpenSettingsURLString)
                if (url != null) {
                    UIApplication.sharedApplication.openURL(url)
                }
            },
        )
    }
