package com.on.turip.data.login.datasource

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.common.safeApiCall
import com.on.turip.data.login.service.AuthService
import javax.inject.Inject

class MemberRemoteDatasource @Inject constructor(
    private val authService: AuthService,
) : MemberDatasource {
    override suspend fun postMigration(): TuripCustomResult<Unit> =
        safeApiCall {
            authService.postMigration()
        }

    override suspend fun postLogout(): TuripCustomResult<Unit> =
        safeApiCall {
            authService.postLogout()
        }

    override suspend fun postWithdraw(): TuripCustomResult<Unit> =
        safeApiCall {
            authService.postWithdraw()
        }
}
