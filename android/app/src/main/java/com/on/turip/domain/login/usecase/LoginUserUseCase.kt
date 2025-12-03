package com.on.turip.domain.login.usecase

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.common.mapCatching
import com.on.turip.domain.login.AuthRepository
import com.on.turip.domain.userstorage.repository.UserStorageRepository
import javax.inject.Inject

class LoginUserUseCase @Inject constructor(
    private val userStorageRepository: UserStorageRepository,
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(idToken: String): TuripCustomResult<Boolean> =
        authRepository.login(idToken).mapCatching { result ->
            userStorageRepository.createTokens(result.authTokens).fold(
                onSuccess = { result.isNewMember },
                onFailure = { error -> throw error },
            )
        }
}
