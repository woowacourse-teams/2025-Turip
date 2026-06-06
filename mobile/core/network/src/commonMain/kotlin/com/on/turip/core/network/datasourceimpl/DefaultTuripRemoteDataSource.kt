package com.on.turip.core.network.datasourceimpl

import com.on.turip.core.common.safeApiCall
import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.network.datasource.TuripRemoteDataSource
import com.on.turip.core.network.dto.turip.PlaceTuripsRequest
import com.on.turip.core.network.dto.turip.TuripCreationResponse
import com.on.turip.core.network.dto.turip.TuripInvitationInformationResponse
import com.on.turip.core.network.dto.turip.TuripInvitationTokenResponse
import com.on.turip.core.network.dto.turip.TuripJoinResponse
import com.on.turip.core.network.dto.turip.TuripMembersResponse
import com.on.turip.core.network.dto.turip.TuripPatchRequest
import com.on.turip.core.network.dto.turip.TuripPlaceOrderRequest
import com.on.turip.core.network.dto.turip.TuripPlacesResponse
import com.on.turip.core.network.dto.turip.TuripPostRequest
import com.on.turip.core.network.dto.turip.TuripResponse
import com.on.turip.core.network.dto.turip.TuripsByPlaceResponse
import com.on.turip.core.network.dto.turip.TuripsResponse
import com.on.turip.core.network.service.TuripService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class DefaultTuripRemoteDataSource(
    private val turipService: TuripService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : TuripRemoteDataSource {
    override suspend fun getTurip(turipId: Long): TuripResult<TuripResponse> =
        withContext(coroutineContext) {
            safeApiCall { turipService.getTurip(turipId) }
        }

    override suspend fun getTurips(): TuripResult<TuripsResponse> =
        withContext(coroutineContext) {
            safeApiCall { turipService.getTurips() }
        }

    override suspend fun getTuripMembers(turipId: Long): TuripResult<TuripMembersResponse> =
        withContext(coroutineContext) {
            safeApiCall { turipService.getTuripMembers(turipId) }
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

    override suspend fun exitTurip(turipId: Long): TuripResult<Unit> =
        withContext(coroutineContext) {
            safeApiCall { turipService.exitTurip(turipId) }
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

    override suspend fun putPlaceTurips(
        placeId: Long,
        placeTuripsRequest: PlaceTuripsRequest,
    ): TuripResult<Unit> = safeApiCall { turipService.putPlaceTurips(placeId, placeTuripsRequest) }

    override suspend fun createInvitationToken(turipId: Long): TuripResult<TuripInvitationTokenResponse> =
        safeApiCall { turipService.postInvitationToken(turipId) }

    override suspend fun joinTurip(turipId: Long): TuripResult<TuripJoinResponse> = safeApiCall { turipService.postJoinTurip(turipId) }

    override suspend fun getInvitationInformation(token: String): TuripResult<TuripInvitationInformationResponse> =
        safeApiCall { turipService.getInvitationInformation(token) }
}
