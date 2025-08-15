package com.on.turip.data.trip.datasource

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.common.safeApiCall
import com.on.turip.data.trip.dto.TripResponse
import com.on.turip.data.trip.service.TripService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class DefaultTripRemoteDataSource(
    private val tripService: TripService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : TripRemoteDataSource {
    override suspend fun getTrip(contentId: Long): TuripCustomResult<TripResponse> =
        withContext(coroutineContext) {
            safeApiCall { tripService.getTrip(contentId) }
        }
}
