package com.on.turip.data.login.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.data.login.service.GuestService
import com.on.turip.data.result.safeApiCall
import javax.inject.Inject

class GuestRemoteDataSource @Inject constructor(
    private val guestService: GuestService,
) : GuestDataSource {
    override suspend fun deleteGuest(): TuripResult<Unit> =
        safeApiCall {
            guestService.deleteGuest()
        }
}
