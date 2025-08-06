package com.on.turip.domain.userstorage.repository

import com.on.turip.domain.userstorage.TuripDeviceIdentifier

interface UserStorageRepository {
    suspend fun createId(turipDeviceIdentifier: TuripDeviceIdentifier)

    suspend fun loadId(): Result<TuripDeviceIdentifier>
}
