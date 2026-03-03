package com.on.turip.domain.login.usecase

import com.on.turip.core.result.ErrorType
import com.on.turip.core.result.TuripResult
import com.on.turip.domain.login.AuthRepository
import com.on.turip.domain.login.AuthResult
import com.on.turip.domain.session.SessionStore
import com.on.turip.domain.session.TokenManager
import timber.log.Timber
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val tokenManager: TokenManager,
    private val sessionStore: SessionStore,
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(idToken: String): TuripResult<Boolean> =
        when (val loginResult = authRepository.login(idToken)) {
            is TuripResult.Failure -> {
                Timber.e("로그인 실패")
                loginResult
            }

            is TuripResult.Success -> {
                val authResult: AuthResult = loginResult.value
                // tokenManager는 로컬저장소 요청으로, Result 반환하도록 설계
                tokenManager
                    .setTokens(authResult.authTokens)
                    .fold(
                        onSuccess = {
                            Timber.e("로그인 성공")
                            sessionStore.setMember()
                            TuripResult.Success(authResult.isNewMember)
                        },
                        onFailure = { exception ->
                            Timber.e("토큰 저장 실패로 인한 로그인 실패")
                            sessionStore.setGuest()
                            TuripResult.Failure(ErrorType.Unknown, exception)
                        },
                    )
            }
        }
}
