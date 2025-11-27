package com.on.turip.domain.login.usecase

import com.on.turip.data.common.onSuccess
import com.on.turip.domain.login.AuthTokens
import com.on.turip.domain.login.LoginRepository
import com.on.turip.domain.userstorage.repository.UserStorageRepository
import javax.inject.Inject

class ReNewTokenUseCase @Inject constructor(
    private val userStorageRepository: UserStorageRepository,
    private val loginRepository: LoginRepository,
) {
    suspend operator fun invoke() {
        userStorageRepository.loadRefreshToken().onSuccess { result: String ->
            loginRepository.requestTokens(result).onSuccess { authTokens: AuthTokens ->
                userStorageRepository.createTokens(authTokens)
            }
        }
    }
}
