package com.on.turip.data.userStorage.dataSource

import com.on.turip.data.userStorage.UserStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class DefaultUserStorageLocalDataSource(
    private val userStorage: UserStorage,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : UserStorageLocalDataSource {
    override suspend fun createId(fid: String): Result<Unit> =
        withContext(coroutineContext) {
            userStorage.saveDeviceId(fid)
        }

    override suspend fun getId(): Result<String?> =
        withContext(coroutineContext) {
            userStorage.getDeviceId()
        }
}
