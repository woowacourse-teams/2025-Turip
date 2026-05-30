package com.on.turip.core.local.userstorage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.on.turip.core.domain.repository.UserStorageRepository
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.first

private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
private val DEVICE_FID_KEY = stringPreferencesKey("device_fid")

class DefaultUserStorageRepository(
    private val dataStore: DataStore<Preferences>,
) : UserStorageRepository {
    override suspend fun loadAccessToken(): Result<String?> =
        runCatching { dataStore.data.first()[ACCESS_TOKEN_KEY] }.recoverCancellation()

    override suspend fun loadRefreshToken(): Result<String?> =
        runCatching { dataStore.data.first()[REFRESH_TOKEN_KEY] }.recoverCancellation()

    override suspend fun createTokens(tokens: AuthTokens): Result<Unit> =
        runCatching {
            dataStore.edit {
                it[ACCESS_TOKEN_KEY] = tokens.accessToken
                it[REFRESH_TOKEN_KEY] = tokens.refreshToken
            }
            Unit
        }.recoverCancellation()

    override suspend fun clearTokens(): Result<Unit> =
        runCatching {
            dataStore.edit {
                it.remove(ACCESS_TOKEN_KEY)
                it.remove(REFRESH_TOKEN_KEY)
            }
            Unit
        }.recoverCancellation()

    override suspend fun loadDeviceFid(): Result<String?> =
        runCatching { dataStore.data.first()[DEVICE_FID_KEY] }.recoverCancellation()

    override suspend fun saveDeviceFid(fid: String): Result<Unit> =
        runCatching {
            dataStore.edit { it[DEVICE_FID_KEY] = fid }
            Unit
        }.recoverCancellation()
}

private suspend fun <T> Result<T>.recoverCancellation(): Result<T> {
    if (isFailure) {
        currentCoroutineContext().ensureActive()
    }
    return this
}
