package com.on.turip.data.userStorage.repository

import com.google.firebase.installations.FirebaseInstallations
import com.on.turip.data.userStorage.dataSource.UserStorageLocalDataSource
import com.on.turip.data.userStorage.toDomain
import com.on.turip.domain.settingsStorage.TuripDeviceIdentifier
import com.on.turip.domain.settingsStorage.repository.SettingsStorageRepository
import kotlinx.coroutines.tasks.await

class DefaultUserStorageRepository(
    private val userStorageLocalDataSource: UserStorageLocalDataSource,
) : SettingsStorageRepository {
    override suspend fun createId(id: String) {
        userStorageLocalDataSource.createId(id)
    }

    override suspend fun loadId(): Result<TuripDeviceIdentifier> =
        userStorageLocalDataSource
            .getId()
            .mapCatching { fid: String? -> fid ?: recreateId() }
            .mapCatching { fid: String -> fid.toDomain() }
            .onFailure {
                // TODO : 예외 발생
            }

    private suspend fun recreateId(): String {
        val newFid = FirebaseInstallations.getInstance().id.await()
        userStorageLocalDataSource.createId(newFid)
        return newFid
    }
}
