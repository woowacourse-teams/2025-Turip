package com.on.turip.data.login.repository

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.login.datasource.AuthDatasource
import com.on.turip.domain.login.AuthRepository
import javax.inject.Inject

class DefaultAuthRepository @Inject constructor(
    private val authDatasource: AuthDatasource,
) : AuthRepository {
    override suspend fun updateMigration(): TuripCustomResult<Unit> = authDatasource.postMigration()

    override suspend fun logout(): TuripCustomResult<Unit> = authDatasource.postLogout()

    override suspend fun withdraw(): TuripCustomResult<Unit> = authDatasource.postWithdraw()
}
