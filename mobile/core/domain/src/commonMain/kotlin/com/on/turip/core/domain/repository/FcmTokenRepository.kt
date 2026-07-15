package com.on.turip.core.domain.repository

import com.on.turip.core.model.result.TuripResult

interface FcmTokenRepository {
    suspend fun registerToken(token: String): TuripResult<Unit>

    suspend fun updateNotificationEnabled(enabled: Boolean): TuripResult<Unit>

    suspend fun getNotificationEnabled(): Boolean
}
