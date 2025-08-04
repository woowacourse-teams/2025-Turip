package com.on.turip.data.userStorage.dataSource

interface UserStorageLocalDataSource {
    suspend fun createId(id: String)

    suspend fun getId(): Result<String?>
}
