package com.on.turip.core.data.userstorage

import com.on.turip.core.domain.repository.UserStorageRepository
import com.on.turip.core.domain.session.AuthTokenCacheController
import com.on.turip.core.domain.session.TokenManager
import com.on.turip.core.model.login.AuthTokens
import com.on.turip.core.model.result.ErrorType
import com.on.turip.core.model.result.TuripResult
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
    private val refreshMutex = Mutex()

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

    // 동시 401 대응: 갱신을 직렬화하고, 이미 갱신된 경우 재발급을 생략한다.
    override suspend fun refreshTokens(
        requestRefreshToken: String,
        requestNewTokens: suspend (refreshToken: String) -> TuripResult<AuthTokens>,
    ): TuripResult<AuthTokens> =
        refreshMutex.withLock {
            val current: AuthTokens =
                _currentTokens
                    ?: return@withLock TuripResult.Failure(
                        errorType = ErrorType.Auth.TokenNotFound,
                        cause = IllegalStateException("저장된 refreshToken이 없습니다."),
                    )

            // 락 대기 중 다른 클라이언트가 이미 갱신을 완료한 경우 → 최신 토큰 재사용
            if (current.refreshToken != requestRefreshToken) {
                return@withLock TuripResult.Success(current)
            }

            when (val result = requestNewTokens(requestRefreshToken)) {
                is TuripResult.Success -> {
                    setTokens(result.value).fold(
                        onSuccess = {
                            Napier.d("refreshToken으로 재발급 받은 토큰 저장 성공")
                            TuripResult.Success(result.value)
                        },
                        onFailure = { throwable ->
                            Napier.e("refreshToken으로 재발급 받은 토큰 저장 실패")
                            TuripResult.Failure(ErrorType.Auth.UnAuthorized, throwable)
                        },
                    )
                }

                is TuripResult.Failure -> {
                    result
                }
            }
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
