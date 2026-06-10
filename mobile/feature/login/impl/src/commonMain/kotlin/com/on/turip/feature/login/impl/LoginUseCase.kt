package com.on.turip.feature.login.impl

import com.on.turip.core.data.session.SessionManager
import com.on.turip.core.domain.repository.AuthRepository
import com.on.turip.core.domain.session.TokenManager
import com.on.turip.core.model.login.AuthResult
import com.on.turip.core.model.result.ErrorType
import com.on.turip.core.model.result.TuripResult
import io.github.aakira.napier.Napier

class LoginUseCase(
    private val tokenManager: TokenManager,
    private val sessionManager: SessionManager,
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(idToken: String): TuripResult<Boolean> =
        when (val loginResult = authRepository.login(idToken)) {
            is TuripResult.Failure -> {
                Napier.e("로그인 실패")
                loginResult
            }

            is TuripResult.Success -> {
                val authResult: AuthResult = loginResult.value
                tokenManager
                    .setTokens(authResult.authTokens)
                    .fold(
                        onSuccess = {
                            Napier.d("로그인 성공")
                            sessionManager.switchToMember()
                            TuripResult.Success(authResult.isMigrationDecided)
                        },
                        onFailure = { exception ->
                            Napier.e("토큰 저장 실패로 인한 로그인 실패", exception)
                            sessionManager.switchToGuest()
                            TuripResult.Failure(ErrorType.Unknown, exception)
                        },
                    )
            }
        }
}
