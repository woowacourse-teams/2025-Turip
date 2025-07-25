package com.on.turip.data.trip.dataSource

import com.on.turip.data.trip.dto.TripResponse

interface TripRemoteDataSource {
    suspend fun getTrip(contentId: Long): Result<TripResponse>
}
