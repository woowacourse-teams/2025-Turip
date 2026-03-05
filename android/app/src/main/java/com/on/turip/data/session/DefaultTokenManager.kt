package com.on.turip.data.session

import com.on.turip.domain.login.AuthTokens
import com.on.turip.domain.session.AuthTokenCacheController
import com.on.turip.domain.session.TokenManager
import com.on.turip.domain.userstorage.repository.UserStorageRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.concurrent.Volatile

/**
 * 인메모리와 로컬 저장소 동기화
 * 토큰에 대한 단일 진입점
 */
@Singleton
class DefaultTokenManager @Inject constructor(
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
                        Timber.e("토큰 업데이트 에러")
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
                        Timber.e("토큰 제거 에러")
                        Result.failure(it)
                    },
                )
        }
    }
}
