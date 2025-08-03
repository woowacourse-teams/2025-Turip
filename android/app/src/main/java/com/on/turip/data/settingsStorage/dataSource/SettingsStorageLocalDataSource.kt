package com.on.turip.data.settingsStorage.dataSource

interface SettingsStorageLocalDataSource {
    suspend fun createId(id: String)

    suspend fun getId(): Result<String?>
}
