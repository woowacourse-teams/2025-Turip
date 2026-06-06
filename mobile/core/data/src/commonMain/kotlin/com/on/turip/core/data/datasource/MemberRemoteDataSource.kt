package com.on.turip.core.data.datasource

import com.on.turip.core.model.result.TuripResult

interface MemberRemoteDataSource {
    suspend fun postMigration(): TuripResult<Unit>

    suspend fun postLogout(): TuripResult<Unit>

    suspend fun deleteMember(): TuripResult<Unit>

    suspend fun postMigrationReject(): TuripResult<Unit>
}
