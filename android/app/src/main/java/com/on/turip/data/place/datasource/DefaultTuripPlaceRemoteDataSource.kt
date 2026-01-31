package com.on.turip.data.place.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.data.place.dto.TuripPlaceOrderRequest
import com.on.turip.data.place.dto.TuripPlacesResponse
import com.on.turip.data.result.safeApiCall
import com.on.turip.data.turip.service.TuripService
import javax.inject.Inject

class DefaultTuripPlaceRemoteDataSource @Inject constructor(
    private val turipService: TuripService,
) : TuripPlaceRemoteDataSource {
    override suspend fun getTuripPlaces(turipId: Long): TuripResult<TuripPlacesResponse> =
        safeApiCall { turipService.getTuripPlaces(turipId) }

    override suspend fun createTuripPlace(
        turipId: Long,
        placeId: Long,
    ): TuripResult<Unit> = safeApiCall { turipService.postTuripPlace(turipId, placeId) }

    override suspend fun deleteTuripPlace(
        turipId: Long,
        placeId: Long,
    ): TuripResult<Unit> = safeApiCall { turipService.deleteTuripPlace(turipId, placeId) }

    override suspend fun patchTuripPlacesOrder(
        turipId: Long,
        turipPlaceOrderRequest: TuripPlaceOrderRequest,
    ): TuripResult<Unit> =
        safeApiCall {
            turipService.patchTuripPlaceOrder(
                turipId,
                turipPlaceOrderRequest,
            )
        }
}
