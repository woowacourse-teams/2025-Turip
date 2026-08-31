package com.on.turip.core.local.fcm

import com.google.firebase.messaging.FirebaseMessaging
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

private const val FCM_TOKEN_WAIT_TIMEOUT_MILLIS = 3_000L

internal actual suspend fun fetchPlatformFcmToken(): String? =
    withTimeoutOrNull(FCM_TOKEN_WAIT_TIMEOUT_MILLIS) {
        try {
            FirebaseMessaging.getInstance().token.await()
        } catch (cancellation: CancellationException) {
            // runCatching은 Throwable을 모두 삼켜 취소를 "토큰 없음"으로 만든다. 취소는 그대로 전파한다.
            throw cancellation
        } catch (exception: Exception) {
            Napier.w("FCM 토큰 조회 실패", exception, tag = "FcmToken")
            null
        }
    }
