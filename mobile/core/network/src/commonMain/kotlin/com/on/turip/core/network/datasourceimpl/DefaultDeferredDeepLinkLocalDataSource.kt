package com.on.turip.core.network.datasourceimpl

import com.on.turip.core.network.datasource.DeferredDeepLinkLocalDataSource

class DefaultDeferredDeepLinkLocalDataSource(
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
