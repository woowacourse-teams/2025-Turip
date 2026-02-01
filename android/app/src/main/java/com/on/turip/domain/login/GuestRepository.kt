package com.on.turip.domain.login

import com.on.turip.core.result.TuripResult

interface GuestRepository {
    suspend fun deleteGuest(): TuripResult<Unit>
}
