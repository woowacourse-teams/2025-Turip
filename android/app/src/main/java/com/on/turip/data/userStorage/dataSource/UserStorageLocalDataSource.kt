package com.on.turip.data.userStorage.dataSource

interface UserStorageLocalDataSource {
    suspend fun createId(fid: String)

    suspend fun getId(): Result<String?>
}
