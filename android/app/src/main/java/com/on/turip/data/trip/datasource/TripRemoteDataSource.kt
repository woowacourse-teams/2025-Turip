package com.on.turip.data.trip.datasource

import com.on.turip.data.trip.dto.TripResponse

interface TripRemoteDataSource {
    suspend fun getTrip(contentId: Long): Result<TripResponse>
}
