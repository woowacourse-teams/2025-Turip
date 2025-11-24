package com.on.turip.domain.login

import com.on.turip.data.common.TuripCustomResult

interface LoginRepository {
    suspend fun login(idToken: String): TuripCustomResult<Unit>
}
