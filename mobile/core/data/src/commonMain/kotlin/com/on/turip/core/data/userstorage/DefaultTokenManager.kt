package com.on.turip.core.data.userstorage

import com.on.turip.core.domain.repository.UserStorageRepository
import com.on.turip.core.domain.session.AuthTokenCacheController
import com.on.turip.core.domain.session.TokenManager
import com.on.turip.core.model.login.AuthTokens
import io.github.aakira.napier.Napier
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.concurrent.Volatile
import kotlin.text.clear

class DefaultTokenManager(
    private val userStorageRepository: UserStorageRepository,
    private val authCacheController: AuthTokenCacheController,
) : TokenManager {
    @Volatile
    private var _currentTokens: AuthTokens? = null
    private val mutex = Mutex()

    override val currentTokens: AuthTokens? get() = _currentTokens

    override suspend fun initialize() {
        mutex.withLock {
            val accessToken = userStorageRepository.loadAccessToken().getOrNull()
            val refreshToken = userStorageRepository.loadRefreshToken().getOrNull()

            _currentTokens =
                if (!accessToken.isNullOrBlank() && !refreshToken.isNullOrBlank()) {
                    AuthTokens(accessToken, refreshToken)
                } else {
                    null
                }
        }
    }

    // 인메모리 & 로컬 저장소 토큰 업데이트 (프레임워크 캐싱된 토큰 초기화)
    override suspend fun setTokens(tokens: AuthTokens): Result<Unit> =
        mutex.withLock {
            userStorageRepository
                .createTokens(tokens)
                .fold(
                    onSuccess = {
                        this._currentTokens = tokens
                        authCacheController.clear()
                        Result.success(Unit)
                    },
                    onFailure = {
                        Napier.e("토큰 업데이트 에러")
                        Result.failure(it)
                    },
                )
        }

    // 인메모리 & 로컬 저장소 토큰 제거 (프레임워크 캐싱된 토큰 초기화)
    override suspend fun clearTokens(): Result<Unit> {
        mutex.withLock {
            _currentTokens = null
            authCacheController.clear()

            return userStorageRepository
                .clearTokens()
                .fold(
                    onSuccess = { Result.success(Unit) },
                    onFailure = {
                        Napier.e("토큰 제거 에러")
                        Result.failure(it)
                    },
                )
        }
    }
}
