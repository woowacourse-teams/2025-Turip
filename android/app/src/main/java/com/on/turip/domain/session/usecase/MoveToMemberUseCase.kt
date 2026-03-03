package com.on.turip.domain.session.usecase

import com.on.turip.domain.login.AuthTokens
import com.on.turip.domain.session.SessionStore
import com.on.turip.domain.session.TokenManager
import javax.inject.Inject

class MoveToMemberUseCase @Inject constructor(
    private val tokenManager: TokenManager,
    private val sessionStore: SessionStore,
) {
    suspend operator fun invoke(tokens: AuthTokens): Result<Unit> {
        val result: Result<Unit> = tokenManager.setTokens(tokens)
        if (result.isSuccess) sessionStore.setMember()

        return result
    }
}
