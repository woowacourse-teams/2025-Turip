package com.on.turip.data.place.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.data.place.dto.TuripPlaceOrderRequest
import com.on.turip.data.place.dto.TuripPlacesResponse
import com.on.turip.data.place.service.PlaceService
import com.on.turip.data.result.safeApiCall
import javax.inject.Inject

class DefaultTuripPlaceRemoteDataSource @Inject constructor(
    private val placeService: PlaceService,
) : TuripPlaceRemoteDataSource {
    override suspend fun getTuripPlaces(turipId: Long): TuripResult<TuripPlacesResponse> =
        safeApiCall { placeService.getTuripPlaces(turipId) }

    override suspend fun createTuripPlace(
        turipId: Long,
        placeId: Long,
    ): TuripResult<Unit> = safeApiCall { placeService.postTuripPlace(turipId, placeId) }

    override suspend fun deleteTuripPlace(
        turipId: Long,
        placeId: Long,
    ): TuripResult<Unit> = safeApiCall { placeService.deleteTuripPlace(turipId, placeId) }

    override suspend fun patchTuripPlacesOrder(
        turipId: Long,
        turipPlaceOrderRequest: TuripPlaceOrderRequest,
    ): TuripResult<Unit> =
        safeApiCall {
            placeService.patchTuripPlaceOrder(
                turipId,
                turipPlaceOrderRequest,
            )
        }
}
