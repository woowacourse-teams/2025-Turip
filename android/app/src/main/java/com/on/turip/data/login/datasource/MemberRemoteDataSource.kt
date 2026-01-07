package com.on.turip.data.login.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.data.login.service.MemberService
import com.on.turip.data.result.safeApiCall
import javax.inject.Inject

class MemberRemoteDataSource @Inject constructor(
    private val memberService: MemberService,
) : MemberDataSource {
    override suspend fun postMigration(): TuripResult<Unit> =
        safeApiCall {
            memberService.postMigration()
        }

    override suspend fun postLogout(): TuripResult<Unit> =
        safeApiCall {
            memberService.postLogout()
        }

    override suspend fun deleteMember(): TuripResult<Unit> =
        safeApiCall {
            memberService.deleteMember()
        }

    override suspend fun deleteGuest(): TuripResult<Unit> =
        safeApiCall {
            memberService.deleteGuest()
        }
}
