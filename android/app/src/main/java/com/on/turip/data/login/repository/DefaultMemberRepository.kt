package com.on.turip.data.login.repository

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.login.datasource.MemberDataSource
import com.on.turip.domain.login.MemberRepository
import javax.inject.Inject

class DefaultMemberRepository @Inject constructor(
    private val memberDataSource: MemberDataSource,
) : MemberRepository {
    override suspend fun updateMigration(): TuripCustomResult<Unit> = memberDataSource.postMigration()

    override suspend fun logout(): TuripCustomResult<Unit> = memberDataSource.postLogout()

    override suspend fun withdraw(): TuripCustomResult<Unit> = memberDataSource.postWithdraw()

    override suspend fun deleteGuestData(): TuripCustomResult<Unit> = memberDataSource.deleteGuestData()
}
