package com.on.turip.data.trip.datasource

import com.on.turip.data.trip.dto.TripResponse
import com.on.turip.data.trip.service.TripService
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultTripRemoteDataSource(
    private val tripService: TripService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : TripRemoteDataSource {
    override suspend fun getTrip(contentId: Long): Result<TripResponse> =
        withContext(coroutineContext) {
            runCatching { tripService.getTrip(contentId) }
        }
}
