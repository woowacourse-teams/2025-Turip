package com.on.turip.data.userstorage.repository

import com.google.firebase.installations.FirebaseInstallations
import com.on.turip.data.userstorage.datasource.UserStorageLocalDataSource
import com.on.turip.data.userstorage.toDomain
import com.on.turip.domain.userstorage.TuripDeviceIdentifier
import com.on.turip.domain.userstorage.repository.UserStorageRepository
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class DefaultUserStorageRepository @Inject constructor(
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
                Timber.e("${it.message}")
            }

    private suspend fun recreateId(): String {
        val newFid: String = FirebaseInstallations.getInstance().id.await()
        userStorageLocalDataSource.createId(newFid)
        return newFid
    }
}
