package com.on.turip.data.trip.repository

import com.on.turip.data.trip.datasource.TripRemoteDataSource
import com.on.turip.data.trip.toDomain
import com.on.turip.domain.trip.Trip
import com.on.turip.domain.trip.repository.TripRepository

class DefaultTripRepository(
    private val tripRemoteDataSource: TripRemoteDataSource,
) : TripRepository {
    override suspend fun loadTripInfo(contentId: Long): Result<Trip> =
        tripRemoteDataSource
            .getTrip(contentId)
            .mapCatching { it.toDomain() }
}
