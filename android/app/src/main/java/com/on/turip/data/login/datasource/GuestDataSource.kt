package com.on.turip.data.login.datasource

import com.on.turip.core.result.TuripResult

interface GuestDataSource {
    suspend fun deleteGuest(): TuripResult<Unit>
}
