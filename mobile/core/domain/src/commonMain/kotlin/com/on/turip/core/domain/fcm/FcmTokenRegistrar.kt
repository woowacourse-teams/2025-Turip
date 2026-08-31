package com.on.turip.core.domain.fcm

import com.on.turip.core.domain.usecase.RegisterFcmTokenUseCase
import com.on.turip.core.model.result.onFailureWithCause
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * FCM 토큰 등록은 실패해도 로그인이나 초기 진입을 실패시키지 않는 부가 작업이다.
 *
 * 호출자를 차단하지 않도록 앱 수명 스코프에서 수행하고, 로그인/스플래시/토큰 갱신 등
 * 모든 진입점이 이 하나의 작업을 공유한다. 등록이 진행 중이면 중복 실행하지 않는다.
 */
class FcmTokenRegistrar(
    private val registerFcmTokenUseCase: RegisterFcmTokenUseCase,
    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) {
    private var registrationJob: Job? = null

    fun register() {
        if (registrationJob?.isActive == true) return

        registrationJob =
            coroutineScope.launch {
                registerFcmTokenUseCase().onFailureWithCause { errorType, cause ->
                    // 다음 앱 시작/로그인 시 재시도되므로 사용자에게 노출하지 않는다.
                    Napier.w("FCM 토큰 등록 실패. errorType=$errorType", cause, tag = "FcmToken")
                }
            }
    }
}
