package com.on.turip.data.place.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.data.place.dto.TuripPlaceOrderRequest
import com.on.turip.data.place.dto.TuripPlacesResponse

interface TuripPlaceRemoteDataSource {
    suspend fun getTuripPlaces(turipId: Long): TuripResult<TuripPlacesResponse>

    suspend fun createTuripPlace(
        turipId: Long,
        placeId: Long,
    ): TuripResult<Unit>

    suspend fun deleteTuripPlace(
        turipId: Long,
        placeId: Long,
    ): TuripResult<Unit>

    suspend fun patchTuripPlacesOrder(
        turipId: Long,
        turipPlaceOrderRequest: TuripPlaceOrderRequest,
    ): TuripResult<Unit>
}
