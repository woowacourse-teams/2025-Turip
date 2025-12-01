package com.on.turip.domain.login.usecase

import com.on.turip.common.AuthState
import com.on.turip.common.UserType
import com.on.turip.domain.login.LoginRepository
import com.on.turip.domain.userstorage.repository.UserStorageRepository
import timber.log.Timber
import javax.inject.Inject

class CheckUserSignedInUseCase @Inject constructor(
    private val userStorageRepository: UserStorageRepository,
    private val loginRepository: LoginRepository,
) {
    suspend operator fun invoke() {
        userStorageRepository.loadAccessToken().onSuccess { result: String? ->
            result?.let { accessToken: String ->
                Timber.d(accessToken)
                AuthState.change(UserType.MEMBER)
                loginRepository
                    .getTokenVerification(accessToken)
                // TODO Result 반환하도록 수정
            } ?: run {
                AuthState.change(UserType.GUEST)
            }
        }
    }
}
