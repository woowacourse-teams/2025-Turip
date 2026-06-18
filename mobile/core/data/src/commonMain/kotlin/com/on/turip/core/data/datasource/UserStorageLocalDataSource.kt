package com.on.turip.core.data.datasource

interface UserStorageLocalDataSource {
    suspend fun createId(fid: String): Result<Unit>

    suspend fun getId(): Result<String?>

    /**
     * access/refresh 토큰을 단일 트랜잭션(하나의 edit 블록)으로 저장하여
     * 둘 중 하나만 쓰인 불일치 상태가 영속되지 않도록 한다.
     */
    suspend fun createTokens(accessToken: String, refreshToken: String): Result<Unit>

    suspend fun getAccessToken(): Result<String?>

    suspend fun getRefreshToken(): Result<String>

    suspend fun deleteTokens(): Result<Unit>
}
