package com.on.turip.domain.trip.repository

import com.on.turip.core.result.TuripResult
import com.on.turip.domain.trip.Trip

interface ContentPlaceRepository {
    suspend fun loadTripInfo(contentId: Long): TuripResult<Trip>
}
