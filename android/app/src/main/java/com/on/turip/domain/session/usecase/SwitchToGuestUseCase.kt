package com.on.turip.domain.session.usecase

import com.on.turip.domain.session.SessionStore
import com.on.turip.domain.session.TokenManager
import timber.log.Timber
import javax.inject.Inject

class SwitchToGuestUseCase @Inject constructor(
    private val tokenManager: TokenManager,
    private val sessionStore: SessionStore,
) {
    suspend operator fun invoke(): Result<Unit> {
        val clearResult: Result<Unit> = tokenManager.clearTokens()
        sessionStore.setGuest()

        return clearResult.onFailure {
            Timber.e("게스트 전환 중 로컬 토큰 삭제 실패")
        }
    }
}
