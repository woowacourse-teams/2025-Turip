package com.on.turip.core.data.datasource

import com.on.turip.core.model.result.TuripResult

interface GuestRemoteDataSource {
    suspend fun deleteGuest(): TuripResult<Unit>
}
