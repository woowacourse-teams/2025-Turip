package com.on.turip.domain.favorite.repository

import com.on.turip.core.result.TuripResult
import com.on.turip.domain.favorite.TuripPlace

interface TuripPlaceRepository {
    suspend fun loadTuripPlaces(turipId: Long): TuripResult<List<TuripPlace>>

    suspend fun createTuripPlace(
        turipId: Long,
        placeId: Long,
    ): TuripResult<Unit>

    suspend fun deleteTuripPlace(
        turipId: Long,
        placeId: Long,
    ): TuripResult<Unit>

    suspend fun updateTuripPlacesOrder(
        turipId: Long,
        updatedOrder: List<Long>,
    ): TuripResult<Unit>
}
