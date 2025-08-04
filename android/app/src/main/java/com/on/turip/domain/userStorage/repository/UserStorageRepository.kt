package com.on.turip.domain.userStorage.repository

import com.on.turip.domain.userStorage.TuripDeviceIdentifier

interface UserStorageRepository {
    suspend fun createId(turipDeviceIdentifier: TuripDeviceIdentifier)

    suspend fun loadId(): Result<TuripDeviceIdentifier>
}
