package com.on.turip.core.network.datasourceimpl

import com.on.turip.core.network.datasource.UserStorageLocalDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
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
        }

    override suspend fun getId(): Result<String?> =
        runCatching {
            withContext(coroutineContext) {
                userStorage.data.first()[deviceFIDKey]
            }
        }

    override suspend fun createAccessToken(accessToken: String): Result<Unit> =
        runCatching {
            withContext(coroutineContext) {
                userStorage.edit { prefs -> prefs[accessTokenKey] = accessToken }
            }
        }

    override suspend fun getAccessToken(): Result<String?> =
        runCatching {
            withContext(coroutineContext) {
                userStorage.data.first()[accessTokenKey]
            }
        }

    override suspend fun createRefreshToken(refreshToken: String): Result<Unit> =
        runCatching {
            withContext(coroutineContext) {
                userStorage.edit { prefs -> prefs[refreshTokenKey] = refreshToken }
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
        }
}