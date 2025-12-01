package com.on.turip.data.login.datasource

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.common.safeApiCall
import com.on.turip.data.login.service.AuthService
import javax.inject.Inject

class AuthRemoteDatasource @Inject constructor(
    private val authService: AuthService,
) : AuthDatasource {
    override suspend fun postMigration(): TuripCustomResult<Unit> =
        safeApiCall {
            authService.postMigration()
        }
}
