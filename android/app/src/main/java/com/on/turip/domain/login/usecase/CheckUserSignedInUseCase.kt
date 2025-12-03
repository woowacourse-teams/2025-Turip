package com.on.turip.domain.login.usecase

import com.on.turip.common.AuthState
import com.on.turip.common.UserType
import com.on.turip.domain.login.AuthRepository
import com.on.turip.domain.userstorage.repository.UserStorageRepository
import javax.inject.Inject

class CheckUserSignedInUseCase @Inject constructor(
    private val userStorageRepository: UserStorageRepository,
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(): Result<Unit> =
        userStorageRepository
            .loadAccessToken()
            .mapCatching { accessToken: String? ->
                if (accessToken != null) {
                    authRepository.getTokenVerification(accessToken)
                    AuthState.change(UserType.MEMBER)
                } else {
                    AuthState.change(UserType.GUEST)
                }
            }.onFailure {
                AuthState.change(UserType.GUEST)
            }
}
