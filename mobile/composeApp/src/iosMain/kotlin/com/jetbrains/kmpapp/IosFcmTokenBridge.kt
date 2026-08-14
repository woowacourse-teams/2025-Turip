package com.on.turip

import com.on.turip.core.data.session.SessionManager
import com.on.turip.core.domain.session.SessionState
import com.on.turip.core.domain.usecase.RegisterFcmTokenUseCase
import com.on.turip.core.local.fcm.provideIosFcmToken
import com.on.turip.core.model.result.onFailureWithCause
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform

private val fcmTokenScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

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

    val registerFcmTokenUseCase: RegisterFcmTokenUseCase = KoinPlatform.getKoin().get()
    fcmTokenScope.launch {
        registerFcmTokenUseCase().onFailureWithCause { errorType, cause ->
            Napier.w("FCM 토큰 갱신 등록 실패. errorType=$errorType", cause, tag = "FcmToken")
        }
    }
}
