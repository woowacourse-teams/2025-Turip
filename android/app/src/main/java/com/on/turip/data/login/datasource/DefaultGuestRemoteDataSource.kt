package com.on.turip.data.login.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.data.login.service.GuestService
import com.on.turip.data.result.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DefaultGuestRemoteDataSource @Inject constructor(
    private val guestService: GuestService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : GuestRemoteDataSource {
    override suspend fun deleteGuest(): TuripResult<Unit> =
        withContext(coroutineContext) {
            safeApiCall { guestService.deleteGuest() }
        }
}
