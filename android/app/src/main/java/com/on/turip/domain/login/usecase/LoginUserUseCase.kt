package com.on.turip.domain.login.usecase

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.common.mapCatching
import com.on.turip.domain.login.LoginRepository
import com.on.turip.domain.userstorage.repository.UserStorageRepository
import javax.inject.Inject

class LoginUserUseCase @Inject constructor(
    private val userStorageRepository: UserStorageRepository,
    private val loginRepository: LoginRepository,
) {
    suspend operator fun invoke(idToken: String): TuripCustomResult<Unit> =
        loginRepository.login(idToken).mapCatching { result ->
            userStorageRepository.createTokens(result.authTokens).fold(
                onSuccess = { },
                onFailure = { error -> throw error },
            )
        }
}
