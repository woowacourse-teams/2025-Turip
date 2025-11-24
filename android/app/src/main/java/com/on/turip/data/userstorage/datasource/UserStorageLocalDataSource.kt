package com.on.turip.data.userstorage.datasource

interface UserStorageLocalDataSource {
    suspend fun createId(fid: String): Result<Unit>

    suspend fun getId(): Result<String?>

    suspend fun createAccessToken(accessToken: String): Result<Unit>

    suspend fun getAccessToken(accessToken: String): Result<String?>

    suspend fun createRefreshToken(refreshToken: String): Result<Unit>

    suspend fun getRefreshToken(refreshToken: String): Result<String?>
}
