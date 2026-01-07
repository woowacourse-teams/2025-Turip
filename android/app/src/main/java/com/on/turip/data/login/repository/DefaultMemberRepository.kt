package com.on.turip.data.login.repository

import com.on.turip.core.result.TuripResult
import com.on.turip.data.login.datasource.MemberDataSource
import com.on.turip.domain.login.MemberRepository
import javax.inject.Inject

class DefaultMemberRepository @Inject constructor(
    private val memberDataSource: MemberDataSource,
) : MemberRepository {
    override suspend fun updateMigration(): TuripResult<Unit> = memberDataSource.postMigration()

    override suspend fun logout(): TuripResult<Unit> = memberDataSource.postLogout()

    override suspend fun deleteMember(): TuripResult<Unit> = memberDataSource.deleteMember()

    override suspend fun deleteGuest(): TuripResult<Unit> = memberDataSource.deleteGuest()
}
