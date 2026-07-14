package com.on.turip.core.domain.session

import com.on.turip.core.model.login.AuthTokens
import com.on.turip.core.model.result.TuripResult

interface TokenManager {
    val currentTokens: AuthTokens?

    suspend fun initialize()

    suspend fun setTokens(tokens: AuthTokens): Result<Unit>

    suspend fun clearTokens(): Result<Unit>

    /**
     * 토큰 갱신을 단일 임계 구역에서 직렬화한다.
     *
     * HTTP/SSE 두 클라이언트가 동시에 401을 받아 같은 refreshToken으로 갱신을 시도해도
     * 실제 재발급 요청은 한 번만 수행된다. 락을 획득했을 때 저장된 refreshToken이 이미
     * 다른 코루틴에 의해 교체된 상태라면 재발급 없이 최신 토큰을 그대로 반환한다.
     */
    suspend fun refreshTokens(
        requestRefreshToken: String,
        requestNewTokens: suspend (refreshToken: String) -> TuripResult<AuthTokens>,
    ): TuripResult<AuthTokens>
}
