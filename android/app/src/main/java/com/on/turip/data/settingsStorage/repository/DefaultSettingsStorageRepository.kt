package com.on.turip.data.settingsStorage.repository

import com.google.firebase.installations.FirebaseInstallations
import com.on.turip.data.settingsStorage.dataSource.SettingsStorageLocalDataSource
import com.on.turip.data.settingsStorage.toDomain
import com.on.turip.domain.settingsStorage.TuripDeviceIdentifier
import com.on.turip.domain.settingsStorage.repository.SettingsStorageRepository
import kotlinx.coroutines.tasks.await

class DefaultSettingsStorageRepository(
    private val settingsStorageLocalDataSource: SettingsStorageLocalDataSource,
) : SettingsStorageRepository {
    override suspend fun createId(id: String) {
        settingsStorageLocalDataSource.createId(id)
    }

    override suspend fun loadId(): Result<TuripDeviceIdentifier> =
        settingsStorageLocalDataSource
            .getId()
            .mapCatching { fid: String? -> fid ?: recreateId() }
            .mapCatching { fid: String -> fid.toDomain() }
            .onFailure {
                // TODO : 예외 발생
            }

    private suspend fun recreateId(): String {
        val newFid = FirebaseInstallations.getInstance().id.await()
        settingsStorageLocalDataSource.createId(newFid)
        return newFid
    }
}
