package com.on.turip

import com.on.turip.core.data.session.SessionManager
import com.on.turip.core.domain.fcm.FcmTokenRegistrar
import com.on.turip.core.domain.session.SessionState
import com.on.turip.core.local.fcm.provideIosFcmToken
import org.koin.mp.KoinPlatform

/**
 * iOS 토큰 전달/갱신 콜백.
 *
 * Android의 `TuripFirebaseMessagingService.onNewToken`과 동일하게,
 * 로그인 상태에서 토큰이 갱신되면 갱신된 토큰을 서버에 재등록한다.
 */
fun provideFcmToken(token: String?) {
    if (!provideIosFcmToken(token)) return

    val sessionManager: SessionManager = KoinPlatform.getKoin().get()
    if (sessionManager.state.value != SessionState.Member) return

    val fcmTokenRegistrar: FcmTokenRegistrar = KoinPlatform.getKoin().get()
    fcmTokenRegistrar.register()
}
