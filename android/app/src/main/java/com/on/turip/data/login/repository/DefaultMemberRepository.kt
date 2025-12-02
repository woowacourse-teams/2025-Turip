package com.on.turip.data.login.repository

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.login.datasource.MemberDatasource
import com.on.turip.domain.login.MemberRepository
import javax.inject.Inject

class DefaultMemberRepository @Inject constructor(
    private val memberDatasource: MemberDatasource,
) : MemberRepository {
    override suspend fun updateMigration(): TuripCustomResult<Unit> = memberDatasource.postMigration()

    override suspend fun logout(): TuripCustomResult<Unit> = memberDatasource.postLogout()

    override suspend fun withdraw(): TuripCustomResult<Unit> = memberDatasource.postWithdraw()
}
