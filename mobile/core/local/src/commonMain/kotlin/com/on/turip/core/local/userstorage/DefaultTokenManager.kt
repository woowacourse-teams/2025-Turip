package com.on.turip.core.local.userstorage

import com.on.turip.core.domain.session.AuthTokenCacheController
import com.on.turip.core.domain.session.TokenManager
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class DefaultTokenManager(
    private val userStorageRepository: UserStorageRepository,
    private val authTokenCacheController: AuthTokenCacheController,
) : TokenManager {
    @kotlin.concurrent.Volatile
    private var _currentTokens: AuthTokens? = null

    private val mutex = Mutex()

    override val currentTokens: AuthTokens?
        get() = _currentTokens

    override suspend fun initialize() {
        mutex.withLock {
            val accessToken = userStorageRepository.loadAccessToken().getOrNull()
            val refreshToken = userStorageRepository.loadRefreshToken().getOrNull()
            _currentTokens = if (accessToken != null && refreshToken != null) {
                AuthTokens(accessToken, refreshToken)
            } else {
                null
            }
        }
    }

    override suspend fun setTokens(tokens: AuthTokens): Result<Unit> =
        mutex.withLock {
            userStorageRepository.createTokens(tokens).also { result ->
                if (result.isSuccess) {
                    _currentTokens = tokens
                    authTokenCacheController.clear()
                }
            }
        }

    override suspend fun clearTokens(): Result<Unit> =
        mutex.withLock {
            _currentTokens = null
            authTokenCacheController.clear()
            userStorageRepository.clearTokens()
        }
}
