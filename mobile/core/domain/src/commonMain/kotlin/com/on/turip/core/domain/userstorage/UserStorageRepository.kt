package com.on.turip.core.domain.userstorage

import com.on.turip.core.model.AuthTokens

interface UserStorageRepository {
    suspend fun loadAccessToken(): Result<String?>

    suspend fun loadRefreshToken(): Result<String?>

    suspend fun createTokens(tokens: AuthTokens): Result<Unit>

    suspend fun clearTokens(): Result<Unit>

    suspend fun loadDeviceFid(): Result<String?>

    suspend fun saveDeviceFid(fid: String): Result<Unit>
}
