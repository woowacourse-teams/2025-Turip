package com.on.turip.data.login.datasource

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.login.dto.LoginResponse

interface ThirdPartyLoginRemoteDatasource {
    suspend fun getIdToken(): TuripCustomResult<LoginResponse>
}
