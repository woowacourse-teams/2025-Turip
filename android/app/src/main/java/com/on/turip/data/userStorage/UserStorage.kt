package com.on.turip.data.userStorage

interface UserStorage {
    suspend fun saveDeviceId(fid: String): Result<Unit>

    suspend fun getDeviceId(): Result<String?>
}
