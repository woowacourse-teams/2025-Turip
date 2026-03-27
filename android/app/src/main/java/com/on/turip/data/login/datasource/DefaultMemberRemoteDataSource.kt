package com.on.turip.data.login.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.data.login.service.MemberService
import com.on.turip.data.result.safeApiCall
import javax.inject.Inject

class DefaultMemberRemoteDataSource @Inject constructor(
    private val memberService: MemberService,
) : MemberRemoteDataSource {
    override suspend fun postMigration(): TuripResult<Unit> = safeApiCall { memberService.postMigration() }

    override suspend fun postLogout(): TuripResult<Unit> = safeApiCall { memberService.postLogout() }

    override suspend fun deleteMember(): TuripResult<Unit> = safeApiCall { memberService.deleteMember() }
}
