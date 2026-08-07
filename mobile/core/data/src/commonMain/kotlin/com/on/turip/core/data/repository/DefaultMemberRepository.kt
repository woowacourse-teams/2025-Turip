package com.on.turip.core.data.repository

import com.on.turip.core.data.datasource.MemberRemoteDataSource
import com.on.turip.core.model.result.TuripResult
import com.on.turip.domain.login.MemberRepository

class DefaultMemberRepository(
    private val memberRemoteDataSource: MemberRemoteDataSource,
) : MemberRepository {
    override suspend fun updateMigration(): TuripResult<Unit> = memberRemoteDataSource.postMigration()

    override suspend fun logout(): TuripResult<Unit> = memberRemoteDataSource.postLogout()

    override suspend fun deleteMember(): TuripResult<Unit> = memberRemoteDataSource.deleteMember()

    override suspend fun rejectMigration(): TuripResult<Unit> = memberRemoteDataSource.postMigrationReject()
}
