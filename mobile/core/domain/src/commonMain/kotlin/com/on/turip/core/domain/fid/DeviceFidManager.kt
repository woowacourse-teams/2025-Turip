package com.on.turip.core.domain.fid

interface DeviceFidManager {
    fun getFid(): String?

    suspend fun ensureInitialized()
}
