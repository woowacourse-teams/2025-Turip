package com.on.turip.data.turip.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.data.result.safeApiCall
import com.on.turip.data.turip.dto.TuripCreationResponse
import com.on.turip.data.turip.dto.TuripPatchRequest
import com.on.turip.data.turip.dto.TuripPlaceOrderRequest
import com.on.turip.data.turip.dto.TuripPlacesResponse
import com.on.turip.data.turip.dto.TuripPostRequest
import com.on.turip.data.turip.dto.TuripsByPlaceResponse
import com.on.turip.data.turip.dto.TuripsResponse
import com.on.turip.data.turip.service.TuripService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DefaultTuripRemoteDataSource @Inject constructor(
    private val turipService: TuripService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : TuripRemoteDataSource {
    override suspend fun getTurips(): TuripResult<TuripsResponse> =
        withContext(coroutineContext) {
            safeApiCall { turipService.getTurips() }
        }

    override suspend fun postTurip(turipPostRequest: TuripPostRequest): TuripResult<TuripCreationResponse> =
        withContext(coroutineContext) {
            safeApiCall { turipService.postTurip(turipPostRequest) }
        }

    override suspend fun patchTurip(
        turipId: Long,
        turipPatchRequest: TuripPatchRequest,
    ): TuripResult<Unit> =
        withContext(coroutineContext) {
            safeApiCall { turipService.patchTurip(turipId, turipPatchRequest) }
        }

    override suspend fun deleteTurip(turipId: Long): TuripResult<Unit> =
        withContext(coroutineContext) {
            safeApiCall { turipService.deleteTurip(turipId) }
        }

    override suspend fun getTuripsByPlaceId(placeId: Long): TuripResult<TuripsByPlaceResponse> =
        withContext(coroutineContext) {
            safeApiCall { turipService.getTuripsByPlaceId(placeId) }
        }

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
