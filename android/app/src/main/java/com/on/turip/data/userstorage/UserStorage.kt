package com.on.turip.data.userstorage

interface UserStorage {
    suspend fun saveDeviceId(fid: String): Result<Unit>

    suspend fun getDeviceId(): Result<String?>
}
