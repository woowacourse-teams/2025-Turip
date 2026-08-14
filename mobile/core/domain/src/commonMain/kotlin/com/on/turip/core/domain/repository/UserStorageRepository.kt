package com.on.turip.core.domain.repository

import com.on.turip.core.model.login.AuthTokens
import com.on.turip.core.model.userstorage.TuripDeviceIdentifier

interface UserStorageRepository {
    suspend fun createId(turipDeviceIdentifier: TuripDeviceIdentifier)

    suspend fun loadId(): Result<TuripDeviceIdentifier>

    suspend fun loadAccessToken(): Result<String?>

    suspend fun loadRefreshToken(): Result<String>

    suspend fun createTokens(tokens: AuthTokens): Result<Unit>

    suspend fun clearTokens(): Result<Unit>
}
