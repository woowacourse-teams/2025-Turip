package com.on.turip.core.local.datasourceimpl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.on.turip.core.data.datasource.UserStorageLocalDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class DefaultUserStorageLocalDataSource(
    private val userStorage: DataStore<Preferences>,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : UserStorageLocalDataSource {
    private val deviceFIDKey: Preferences.Key<String> = stringPreferencesKey("device_fid")
    private val accessTokenKey: Preferences.Key<String> = stringPreferencesKey("access_token")
    private val refreshTokenKey: Preferences.Key<String> = stringPreferencesKey("refresh_token")

    override suspend fun createId(fid: String): Result<Unit> =
        runCatching {
            withContext(coroutineContext) {
                userStorage.edit { prefs -> prefs[deviceFIDKey] = fid }
            }
            Unit
        }

    override suspend fun getId(): Result<String?> =
        runCatching {
            withContext(coroutineContext) {
                userStorage.data.first()[deviceFIDKey]
            }
        }

    override suspend fun createTokens(accessToken: String, refreshToken: String): Result<Unit> =
        runCatching {
            withContext(coroutineContext) {
                userStorage.edit { prefs ->
                    prefs[accessTokenKey] = accessToken
                    prefs[refreshTokenKey] = refreshToken
                }
            }
            Unit
        }

    override suspend fun getAccessToken(): Result<String?> =
        runCatching {
            withContext(coroutineContext) {
                userStorage.data.first()[accessTokenKey]
            }
        }

    override suspend fun getRefreshToken(): Result<String> =
        runCatching {
            withContext(coroutineContext) {
                userStorage.data.first()[refreshTokenKey]
                    ?: throw IllegalArgumentException("refreshToken이 존재하지 않습니다.")
            }
        }

    override suspend fun deleteTokens(): Result<Unit> =
        runCatching {
            withContext(coroutineContext) {
                userStorage.edit { prefs ->
                    prefs.remove(accessTokenKey)
                    prefs.remove(refreshTokenKey)
                }
            }
            Unit
        }
}
