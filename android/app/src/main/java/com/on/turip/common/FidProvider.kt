package com.on.turip.common

import com.on.turip.domain.userstorage.repository.UserStorageRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking

@Singleton
class FidProvider @Inject constructor(
    private val userStorageRepository: UserStorageRepository,
) {
    @Volatile
    private var _cachedFid: String? = null

    fun init() {
        if (_cachedFid != null) return

        _cachedFid =
            runBlocking {
                userStorageRepository
                    .loadId()
                    .getOrNull()
                    ?.fid
            } ?: "unknown"
    }

    val cachedFid: String get() = _cachedFid ?: "unknown"
}
