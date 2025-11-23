package com.on.turip.data.login.datasource

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.login.dto.LoginJwtTokenResponse

interface LoginDatasource {
    suspend fun postIdToken(idToken: String): TuripCustomResult<LoginJwtTokenResponse>
}
