package com.on.turip.domain.login

import com.on.turip.core.model.result.TuripResult

interface GuestRepository {
    suspend fun deleteGuest(): TuripResult<Unit>
}
