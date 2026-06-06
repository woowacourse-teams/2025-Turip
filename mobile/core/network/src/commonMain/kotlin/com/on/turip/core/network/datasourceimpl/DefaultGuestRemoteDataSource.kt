package com.on.turip.core.network.datasourceimpl

import com.on.turip.core.common.safeApiCall
import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.network.datasource.GuestRemoteDataSource
import com.on.turip.core.network.service.GuestService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class DefaultGuestRemoteDataSource(
    private val guestService: GuestService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : GuestRemoteDataSource {
    override suspend fun deleteGuest(): TuripResult<Unit> =
        withContext(coroutineContext) {
            safeApiCall { guestService.deleteGuest() }
        }
}
