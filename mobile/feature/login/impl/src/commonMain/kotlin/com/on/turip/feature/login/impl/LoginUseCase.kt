package com.on.turip.feature.login.impl

import com.on.turip.core.data.session.SessionManager
import com.on.turip.core.domain.repository.AuthRepository
import com.on.turip.core.domain.session.TokenManager
import com.on.turip.core.domain.usecase.RegisterFcmTokenUseCase
import com.on.turip.core.model.login.AuthResult
import com.on.turip.core.model.result.ErrorType
import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.model.result.onFailureWithCause
import io.github.aakira.napier.Napier

class LoginUseCase(
    private val tokenManager: TokenManager,
    private val sessionManager: SessionManager,
    private val authRepository: AuthRepository,
    private val registerFcmTokenUseCase: RegisterFcmTokenUseCase,
) {
    suspend operator fun invoke(credential: SocialCredential): TuripResult<Boolean> =
        handleLoginResult(
            when (credential) {
                is SocialCredential.Google -> authRepository.login(credential.idToken)
                is SocialCredential.Apple ->
                    authRepository.loginWithApple(idToken = credential.idToken, nonce = credential.rawNonce)
            },
        )

    private suspend fun handleLoginResult(loginResult: TuripResult<AuthResult>): TuripResult<Boolean> =
        when (loginResult) {
            is TuripResult.Failure -> {
                Napier.e(
                    message = "로그인 실패. errorType=${loginResult.errorType}",
                    throwable = loginResult.cause,
                )
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
                            registerFcmTokenUseCase().onFailureWithCause { errorType, cause ->
                                Napier.w("FCM 토큰 등록 실패. errorType=$errorType", cause, tag = "FcmToken")
                            }
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
