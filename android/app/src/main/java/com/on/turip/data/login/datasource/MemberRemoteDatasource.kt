package com.on.turip.data.login.datasource

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.common.safeApiCall
import com.on.turip.data.login.service.MemberService
import javax.inject.Inject

class MemberRemoteDatasource @Inject constructor(
    private val memberService: MemberService,
) : MemberDatasource {
    override suspend fun postMigration(): TuripCustomResult<Unit> =
        safeApiCall {
            memberService.postMigration()
        }

    override suspend fun postLogout(): TuripCustomResult<Unit> =
        safeApiCall {
            memberService.postLogout()
        }

    override suspend fun postWithdraw(): TuripCustomResult<Unit> =
        safeApiCall {
            memberService.postWithdraw()
        }
}
