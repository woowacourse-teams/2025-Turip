package com.on.turip.domain.trip.repository

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.domain.trip.Trip

interface TripRepository {
    suspend fun loadTripInfo(contentId: Long): TuripCustomResult<Trip>
}
