package com.on.turip.domain.session

import com.on.turip.domain.login.AuthTokens

/**
 * 인증 토큰을 인메모리 및 로컬 저장소와 동기화하여 관리하는 컴포넌트
 */
interface TokenManager {
    fun currentTokens(): AuthTokens?

    suspend fun initialize()

    suspend fun setTokens(tokens: AuthTokens): Result<Unit>

    suspend fun clearTokens(): Result<Unit>
}
