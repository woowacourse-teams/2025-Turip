package com.on.turip.core.local.fid

import com.on.turip.core.domain.fid.DeviceFidManager
import com.on.turip.core.domain.repository.UserStorageRepository
import io.github.aakira.napier.Napier
import kotlin.concurrent.Volatile
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class DefaultDeviceFidManager(
    private val userStorageRepository: UserStorageRepository,
) : DeviceFidManager {
    @Volatile
    private var cachedFid: String? = null

    override fun getFid(): String? = cachedFid

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun ensureInitialized() {
        if (cachedFid != null) {
            Napier.d("FID already cached: $cachedFid", tag = "DeviceFid")
            return
        }
        val stored = userStorageRepository.loadDeviceFid().getOrNull()
        if (stored != null) {
            cachedFid = stored
            Napier.d("FID loaded from storage: $stored", tag = "DeviceFid")
            return
        }
        val platformFid = fetchPlatformFid()
        Napier.d("Firebase FID: $platformFid", tag = "DeviceFid")
        val newFid = platformFid ?: Uuid.random().toString()
        userStorageRepository.saveDeviceFid(newFid)
        cachedFid = newFid
        Napier.d("FID saved: $newFid", tag = "DeviceFid")
    }
}
