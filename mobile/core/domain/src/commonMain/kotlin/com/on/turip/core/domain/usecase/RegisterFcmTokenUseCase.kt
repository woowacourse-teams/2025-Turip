package com.on.turip.core.domain.usecase

import com.on.turip.core.domain.fcm.FcmTokenProvider
import com.on.turip.core.domain.repository.FcmTokenRepository
import com.on.turip.core.model.result.ErrorType
import com.on.turip.core.model.result.TuripResult
import io.github.aakira.napier.Napier

class RegisterFcmTokenUseCase(
    private val fcmTokenProvider: FcmTokenProvider,
    private val fcmTokenRepository: FcmTokenRepository,
) {
    suspend operator fun invoke(): TuripResult<Unit> {
        val token = fcmTokenProvider.fetchToken()
        if (token == null) {
            Napier.w("FCM 토큰을 가져오지 못해 등록을 건너뜁니다.", tag = "FcmToken")
            return TuripResult.Failure(ErrorType.Unknown, IllegalStateException("FCM token unavailable"))
        }
        return fcmTokenRepository.registerToken(token)
    }
}
