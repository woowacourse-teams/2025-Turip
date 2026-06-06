package com.on.turip.core.data.datasource

import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.data.dto.turip.PlaceTuripsRequest
import com.on.turip.core.data.dto.turip.TuripCreationResponse
import com.on.turip.core.data.dto.turip.TuripInvitationInformationResponse
import com.on.turip.core.data.dto.turip.TuripInvitationTokenResponse
import com.on.turip.core.data.dto.turip.TuripJoinResponse
import com.on.turip.core.data.dto.turip.TuripMembersResponse
import com.on.turip.core.data.dto.turip.TuripPatchRequest
import com.on.turip.core.data.dto.turip.TuripPlaceOrderRequest
import com.on.turip.core.data.dto.turip.TuripPlacesResponse
import com.on.turip.core.data.dto.turip.TuripPostRequest
import com.on.turip.core.data.dto.turip.TuripResponse
import com.on.turip.core.data.dto.turip.TuripsByPlaceResponse
import com.on.turip.core.data.dto.turip.TuripsResponse

interface TuripRemoteDataSource {
    suspend fun getTurip(turipId: Long): TuripResult<TuripResponse>

    suspend fun getTurips(): TuripResult<TuripsResponse>

    suspend fun getTuripMembers(turipId: Long): TuripResult<TuripMembersResponse>

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
}
