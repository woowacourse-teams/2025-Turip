package com.on.turip.core.network.datasource

interface UserStorageLocalDataSource {
    suspend fun createId(fid: String): Result<Unit>

    suspend fun getId(): Result<String?>

    suspend fun createAccessToken(accessToken: String): Result<Unit>

    suspend fun getAccessToken(): Result<String?>

    suspend fun createRefreshToken(refreshToken: String): Result<Unit>

    suspend fun getRefreshToken(): Result<String>

    suspend fun deleteTokens(): Result<Unit>
}
