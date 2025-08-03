package com.on.turip.data.settingsStorage.dataSource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class DefaultSettingsStorageLocalDataSource(
    private val context: Context,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : SettingsStorageLocalDataSource {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings_storage")
    private val DEVICE_FID_KEY: Preferences.Key<String> = stringPreferencesKey("device_fid")

    override suspend fun createId(id: String): Unit =
        withContext(coroutineContext) {
            context.dataStore.edit { prefs: MutablePreferences -> prefs[DEVICE_FID_KEY] = id }
        }

    override suspend fun getId(): Result<String?> =
        withContext(coroutineContext) {
            runCatching { context.dataStore.data.first()[DEVICE_FID_KEY] }
        }
}
