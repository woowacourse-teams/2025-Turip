package com.on.turip.data.invitation.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DefaultDeferredDeepLinkLocalDataSource @Inject constructor(
    private val userStorage: DataStore<Preferences>,
) : DeferredDeepLinkLocalDataSource {
    // install referrer 가 처리 되었는지 여부
    private val installReferrerHandledKey: Preferences.Key<Boolean> =
        booleanPreferencesKey("install_referrer_handled")

    override suspend fun isInstallReferrerHandled(): Boolean = userStorage.data.first()[installReferrerHandledKey] == true

    override suspend fun markInstallReferrerHandled() {
        userStorage.edit { prefs -> prefs[installReferrerHandledKey] = true }
    }
}
