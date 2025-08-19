package com.on.turip.domain.trip.repository

import com.on.turip.domain.trip.Trip

interface TripRepository {
    suspend fun loadTripInfo(contentId: Long): Result<Trip>
}
