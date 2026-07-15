package com.on.turip.core.local.fcm

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

internal actual suspend fun fetchPlatformFcmToken(): String? =
    runCatching { FirebaseMessaging.getInstance().token.await() }.getOrNull()
