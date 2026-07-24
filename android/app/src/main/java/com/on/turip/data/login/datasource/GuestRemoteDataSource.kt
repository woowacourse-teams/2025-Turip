package com.on.turip.data.login.datasource

import com.on.turip.core.result.TuripResult

interface GuestRemoteDataSource {
    suspend fun deleteGuest(): TuripResult<Unit>
}
