package com.on.turip.domain.login

import com.on.turip.data.common.TuripCustomResult

interface MemberRepository {
    suspend fun updateMigration(): TuripCustomResult<Unit>

    suspend fun logout(): TuripCustomResult<Unit>

    suspend fun deleteMember(): TuripCustomResult<Unit>

    suspend fun deleteGuest(): TuripCustomResult<Unit>
}
