package com.on.turip.data.login.datasource

import com.on.turip.data.common.TuripCustomResult

interface MemberDatasource {
    suspend fun postMigration(): TuripCustomResult<Unit>

    suspend fun postLogout(): TuripCustomResult<Unit>

    suspend fun postWithdraw(): TuripCustomResult<Unit>
}
