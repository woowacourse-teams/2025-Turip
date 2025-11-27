package com.on.turip.domain.login.usecase

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.domain.login.AuthTokens
import com.on.turip.domain.login.LoginRepository
import com.on.turip.domain.userstorage.repository.UserStorageRepository
import javax.inject.Inject

class ReNewTokenUseCase @Inject constructor(
    private val userStorageRepository: UserStorageRepository,
    private val loginRepository: LoginRepository,
) {
    suspend operator fun invoke(): Result<Unit> =
        userStorageRepository
            .loadRefreshToken()
            .mapCatching { refreshToken ->
                val tokens: TuripCustomResult<AuthTokens> =
                    loginRepository.requestTokens(refreshToken)
                if (tokens is TuripCustomResult.Success) {
                    userStorageRepository.createTokens(tokens.data)
                    Unit
                } else {
                    throw IllegalStateException("토큰 재요청 실패")
                }
            }
}
