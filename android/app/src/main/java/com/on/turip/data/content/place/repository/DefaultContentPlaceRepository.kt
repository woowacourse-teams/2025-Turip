package com.on.turip.data.content.place.repository

import com.on.turip.core.result.TuripResult
import com.on.turip.core.result.mapCatching
import com.on.turip.data.content.place.datasource.ContentPlaceRemoteDataSource
import com.on.turip.data.content.place.toDomain
import com.on.turip.domain.trip.Trip
import com.on.turip.domain.trip.repository.ContentPlaceRepository
import javax.inject.Inject

class DefaultContentPlaceRepository @Inject constructor(
    private val contentPlaceRemoteDataSource: ContentPlaceRemoteDataSource,
) : ContentPlaceRepository {
    override suspend fun loadTripInfo(contentId: Long): TuripResult<Trip> =
        contentPlaceRemoteDataSource
            .getTrip(contentId)
            .mapCatching { it.toDomain() }
}
