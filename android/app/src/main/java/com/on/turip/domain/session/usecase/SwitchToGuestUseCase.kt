package com.on.turip.domain.session.usecase

import com.on.turip.domain.session.SessionStore
import com.on.turip.domain.session.TokenManager
import javax.inject.Inject

class SwitchToGuestUseCase @Inject constructor(
    private val tokenManager: TokenManager,
    private val sessionStore: SessionStore,
) {
    suspend operator fun invoke() {
        tokenManager.clearTokens()
        sessionStore.setGuest()
    }
}
