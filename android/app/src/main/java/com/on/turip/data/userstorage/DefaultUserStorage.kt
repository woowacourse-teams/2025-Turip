package com.on.turip.data.userstorage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class DefaultUserStorage(
    private val context: Context,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : UserStorage {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_storage")
    private val deviceFIDKey: Preferences.Key<String> = stringPreferencesKey("device_fid")

    override suspend fun saveDeviceId(fid: String): Result<Unit> =
        runCatching {
            withContext(coroutineContext) {
                context.dataStore.edit { prefs -> prefs[deviceFIDKey] = fid }
            }
        }

    override suspend fun getDeviceId(): Result<String?> =
        withContext(coroutineContext) {
            runCatching { context.dataStore.data.first()[deviceFIDKey] }
        }
}
