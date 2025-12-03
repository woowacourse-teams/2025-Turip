package com.on.turip.data.login.datasource

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.common.safeApiCall
import com.on.turip.data.login.service.MemberService
import javax.inject.Inject

class MemberRemoteDataSource @Inject constructor(
    private val memberService: MemberService,
) : MemberDataSource {
    override suspend fun postMigration(): TuripCustomResult<Unit> =
        safeApiCall {
            memberService.postMigration()
        }

    override suspend fun postLogout(): TuripCustomResult<Unit> =
        safeApiCall {
            memberService.postLogout()
        }

    override suspend fun deleteMemberData(): TuripCustomResult<Unit> =
        safeApiCall {
            memberService.deleteMemberData()
        }

    override suspend fun deleteGuestData(): TuripCustomResult<Unit> =
        safeApiCall {
            memberService.deleteGuestData()
        }
}
