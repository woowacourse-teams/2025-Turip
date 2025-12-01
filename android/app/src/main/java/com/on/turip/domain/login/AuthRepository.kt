package com.on.turip.domain.login

import com.on.turip.data.common.TuripCustomResult

interface AuthRepository {
    suspend fun updateMigration(): TuripCustomResult<Unit>
}
