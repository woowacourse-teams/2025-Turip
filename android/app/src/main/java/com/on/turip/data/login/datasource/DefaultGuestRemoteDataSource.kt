package com.on.turip.data.login.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.data.login.service.GuestService
import com.on.turip.data.result.safeApiCall
import javax.inject.Inject

class DefaultGuestRemoteDataSource @Inject constructor(
    private val guestService: GuestService,
) : GuestRemoteDataSource {
    override suspend fun deleteGuest(): TuripResult<Unit> = safeApiCall { guestService.deleteGuest() }
}
