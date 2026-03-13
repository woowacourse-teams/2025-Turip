package com.on.turip.data.turip.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.data.turip.dto.PlaceTuripsRequest
import com.on.turip.data.turip.dto.TuripCreationResponse
import com.on.turip.data.turip.dto.TuripInvitationInformationResponse
import com.on.turip.data.turip.dto.TuripInvitationTokenResponse
import com.on.turip.data.turip.dto.TuripJoinResponse
import com.on.turip.data.turip.dto.TuripPatchRequest
import com.on.turip.data.turip.dto.TuripPlaceOrderRequest
import com.on.turip.data.turip.dto.TuripPlacesResponse
import com.on.turip.data.turip.dto.TuripPostRequest
import com.on.turip.data.turip.dto.TuripResponse
import com.on.turip.data.turip.dto.TuripsByPlaceResponse
import com.on.turip.data.turip.dto.TuripsResponse
import com.on.turip.domain.turip.TuripStreamEvent
import kotlinx.coroutines.flow.Flow

interface TuripRemoteDataSource {
    suspend fun getTurip(turipId: Long): TuripResult<TuripResponse>

    suspend fun getTurips(): TuripResult<TuripsResponse>

    suspend fun postTurip(turipPostRequest: TuripPostRequest): TuripResult<TuripCreationResponse>

    suspend fun patchTurip(
        turipId: Long,
        turipPatchRequest: TuripPatchRequest,
    ): TuripResult<Unit>

    suspend fun deleteTurip(turipId: Long): TuripResult<Unit>

    suspend fun exitTurip(turipId: Long): TuripResult<Unit>

    suspend fun getTuripsByPlaceId(placeId: Long): TuripResult<TuripsByPlaceResponse>

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

    suspend fun putPlaceTurips(
        placeId: Long,
        placeTuripsRequest: PlaceTuripsRequest,
    ): TuripResult<Unit>

    suspend fun createInvitationToken(turipId: Long): TuripResult<TuripInvitationTokenResponse>

    suspend fun joinTurip(turipId: Long): TuripResult<TuripJoinResponse>

    suspend fun getInvitationInformation(token: String): TuripResult<TuripInvitationInformationResponse>

    fun streamTuripEvents(turipId: Long): Flow<TuripResult<TuripStreamEvent>>
}
