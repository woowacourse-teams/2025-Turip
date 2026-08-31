package com.on.turip.core.local.datasourceimpl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.on.turip.core.data.datasource.NotificationSettingLocalDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class DefaultNotificationSettingLocalDataSource(
    private val userStorage: DataStore<Preferences>,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : NotificationSettingLocalDataSource {
    private val notificationEnabledKey: Preferences.Key<Boolean> = booleanPreferencesKey("notification_enabled")

    override suspend fun saveNotificationEnabled(enabled: Boolean): Result<Unit> =
        runCatching {
            withContext(coroutineContext) {
                userStorage.edit { prefs -> prefs[notificationEnabledKey] = enabled }
            }
            Unit
        }

    override suspend fun getNotificationEnabled(): Result<Boolean?> =
        runCatching {
            withContext(coroutineContext) {
                userStorage.data.first()[notificationEnabledKey]
            }
        }
}
