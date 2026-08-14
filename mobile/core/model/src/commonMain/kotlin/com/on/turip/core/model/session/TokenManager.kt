package com.on.turip.core.model.session

import com.on.turip.core.model.login.AuthTokens

/**
 * 인증 토큰을 인메모리 및 로컬 저장소와 동기화하여 관리하는 컴포넌트
 */
interface TokenManager {
    val currentTokens: AuthTokens?

    suspend fun initialize()

    suspend fun setTokens(tokens: AuthTokens): Result<Unit>

    suspend fun clearTokens(): Result<Unit>
}
