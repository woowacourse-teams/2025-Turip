package com.on.turip.core.local.fcm

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeoutOrNull

private const val FCM_TOKEN_WAIT_TIMEOUT_MILLIS = 3_000L

private val iosFcmToken = CompletableDeferred<String?>()

fun provideIosFcmToken(token: String?) {
    if (!iosFcmToken.isCompleted) {
        iosFcmToken.complete(token?.takeIf { it.isNotBlank() })
    }
}

internal actual suspend fun fetchPlatformFcmToken(): String? =
    withTimeoutOrNull(FCM_TOKEN_WAIT_TIMEOUT_MILLIS) {
        iosFcmToken.await()
    }
