package com.on.turip.domain.login

import com.on.turip.core.result.TuripResult

interface MemberRepository {
    suspend fun updateMigration(): TuripResult<Unit>

    suspend fun logout(): TuripResult<Unit>

    suspend fun deleteMember(): TuripResult<Unit>

    suspend fun deleteGuest(): TuripResult<Unit>
}
