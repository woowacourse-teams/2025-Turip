package com.on.turip.data.userstorage.datasource

interface UserStorageLocalDataSource {
    suspend fun createId(fid: String): Result<Unit>

    suspend fun getId(): Result<String?>
}
