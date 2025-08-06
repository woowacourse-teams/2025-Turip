package com.on.turip.data.userstorage.repository

import com.google.firebase.installations.FirebaseInstallations
import com.on.turip.data.userstorage.datasource.UserStorageLocalDataSource
import com.on.turip.data.userstorage.toDomain
import com.on.turip.domain.userstorage.TuripDeviceIdentifier
import com.on.turip.domain.userstorage.repository.UserStorageRepository
import kotlinx.coroutines.tasks.await

class DefaultUserStorageRepository(
    private val userStorageLocalDataSource: UserStorageLocalDataSource,
) : UserStorageRepository {
    override suspend fun createId(turipDeviceIdentifier: TuripDeviceIdentifier) {
        userStorageLocalDataSource.createId(turipDeviceIdentifier.fid)
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
        val newFid: String = FirebaseInstallations.getInstance().id.await()
        userStorageLocalDataSource.createId(newFid)
        return newFid
    }
}
