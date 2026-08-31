package com.on.turip.core.local.fcm

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull

private const val FCM_TOKEN_WAIT_TIMEOUT_MILLIS = 3_000L

private val iosFcmToken: MutableStateFlow<String?> = MutableStateFlow(null)

/**
 * iOS는 토큰이 갱신될 때마다 콜백이 다시 들어온다.
 *
 * 유효한 토큰만 최신 값으로 보관한다. null/blank 콜백은 무시하므로 토큰 대기를 끝내지 않고,
 * 이후 전달되는 갱신 토큰이 이전 토큰을 대체한다.
 *
 * @return 유효한 토큰을 보관했으면 true.
 */
fun provideIosFcmToken(token: String?): Boolean {
    val validToken: String = token?.takeIf(String::isNotBlank) ?: return false
    iosFcmToken.value = validToken
    return true
}

internal actual suspend fun fetchPlatformFcmToken(): String? =
    withTimeoutOrNull(FCM_TOKEN_WAIT_TIMEOUT_MILLIS) {
        iosFcmToken.filterNotNull().first()
    }
