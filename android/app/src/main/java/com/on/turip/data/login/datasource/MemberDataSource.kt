package com.on.turip.data.login.datasource

import com.on.turip.core.result.TuripResult

interface MemberDataSource {
    suspend fun postMigration(): TuripResult<Unit>

    suspend fun postLogout(): TuripResult<Unit>

    suspend fun deleteMember(): TuripResult<Unit>

    suspend fun deleteGuest(): TuripResult<Unit>
}
