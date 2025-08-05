package com.on.turip.domain.settingsStorage.repository

import com.on.turip.domain.settingsStorage.TuripDeviceIdentifier

interface UserStorageRepository {
    suspend fun createId(id: String)

    suspend fun loadId(): Result<TuripDeviceIdentifier>
}
