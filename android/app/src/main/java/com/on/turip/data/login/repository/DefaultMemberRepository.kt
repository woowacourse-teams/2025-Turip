package com.on.turip.data.login.repository

import com.on.turip.core.result.TuripResult
import com.on.turip.data.login.datasource.MemberRemoteDataSource
import com.on.turip.domain.login.MemberRepository
import javax.inject.Inject

class DefaultMemberRepository @Inject constructor(
    private val memberRemoteDataSource: MemberRemoteDataSource,
) : MemberRepository {
    override suspend fun updateMigration(): TuripResult<Unit> = memberRemoteDataSource.postMigration()

    override suspend fun logout(): TuripResult<Unit> = memberRemoteDataSource.postLogout()

    override suspend fun deleteMember(): TuripResult<Unit> = memberRemoteDataSource.deleteMember()

    override suspend fun rejectMigration(): TuripResult<Unit> = memberRemoteDataSource.postMigrationReject()
}
