package com.on.turip.data.userstorage.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DefaultUserStorageLocalDataSource @Inject constructor(
    private val userStorage: DataStore<Preferences>,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : UserStorageLocalDataSource {
    private val deviceFIDKey: Preferences.Key<String> = stringPreferencesKey("device_fid")

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
}
