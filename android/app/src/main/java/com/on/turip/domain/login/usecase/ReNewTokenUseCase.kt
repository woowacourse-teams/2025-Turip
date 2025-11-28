package com.on.turip.domain.login.usecase

import com.on.turip.common.AuthState
import com.on.turip.common.UserType
import com.on.turip.data.common.TuripCustomResult
import com.on.turip.domain.login.LoginRepository
import com.on.turip.domain.userstorage.repository.UserStorageRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import javax.inject.Inject

class ReNewTokenUseCase @Inject constructor(
    private val userStorageRepository: UserStorageRepository,
    private val loginRepository: LoginRepository,
) {
    private val mutex = Mutex()

    private var isRefreshing = false

    suspend operator fun invoke(): Result<Unit> =
        runCatching {
            mutex.withLock {
                if (isRefreshing) {
                    Timber.d("이미 토큰 재발급 진행 중 → 대기 없이 통과")
                    return@withLock
                }

                isRefreshing = true
                try {
                    val refreshTokenResult = userStorageRepository.loadRefreshToken()
                    val refreshToken = refreshTokenResult.getOrThrow()

                    when (val tokens = loginRepository.requestTokens(refreshToken)) {
                        is TuripCustomResult.Success -> {
                            Timber.d("토큰 재요청 성공")
                            userStorageRepository.createTokens(tokens.data)
                            AuthState.change(UserType.MEMBER)
                        }

                        else -> {
                            AuthState.change(UserType.GUEST)
                            throw IllegalStateException("토큰 재요청 실패")
                        }
                    }
                } finally {
                    isRefreshing = false
                }
            }
        }
}
