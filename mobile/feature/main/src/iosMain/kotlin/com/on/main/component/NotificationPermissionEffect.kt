package com.on.turip.feature.main.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.UIKit.UIApplication
import platform.UIKit.registerForRemoteNotifications
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume

@Composable
actual fun NotificationPermissionEffect() {
    LaunchedEffect(Unit) {
        val isGranted = requestNotificationAuthorization()
        if (isGranted) {
            // APNs 디바이스 토큰 발급 요청. 이 토큰이 있어야 Firebase가 FCM 토큰을 발급할 수 있다.
            UIApplication.sharedApplication.registerForRemoteNotifications()
        }
    }
}

private suspend fun requestNotificationAuthorization(): Boolean =
    suspendCancellableCoroutine { continuation ->
        UNUserNotificationCenter.currentNotificationCenter().requestAuthorizationWithOptions(
            options = UNAuthorizationOptionAlert or UNAuthorizationOptionBadge or UNAuthorizationOptionSound,
            completionHandler = { granted, _ -> continuation.resume(granted) },
        )
    }
