package com.on.turip.domain.userstorage.repository

import com.on.turip.domain.login.AuthTokens
import com.on.turip.domain.userstorage.TuripDeviceIdentifier

interface UserStorageRepository {
    suspend fun createId(turipDeviceIdentifier: TuripDeviceIdentifier)

    suspend fun loadId(): Result<TuripDeviceIdentifier>

    suspend fun loadAccessToken(): Result<String?>

    suspend fun loadRefreshToken(): Result<String>

    suspend fun createTokens(tokens: AuthTokens): Result<Unit>
}
