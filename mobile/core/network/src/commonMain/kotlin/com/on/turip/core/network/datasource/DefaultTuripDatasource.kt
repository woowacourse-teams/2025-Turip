package com.on.turip.core.network.datasource

import com.on.turip.core.network.dto.turip.PlaceTuripsRequest
import com.on.turip.core.network.dto.turip.TuripByPlaceResponse
import com.on.turip.core.network.dto.turip.TuripCreationResponse
import com.on.turip.core.network.dto.turip.TuripInvitationInformationResponse
import com.on.turip.core.network.dto.turip.TuripInvitationTokenResponse
import com.on.turip.core.network.dto.turip.TuripMemberResponse
import com.on.turip.core.network.dto.turip.TuripPatchRequest
import com.on.turip.core.network.dto.turip.TuripPlaceOrderRequest
import com.on.turip.core.network.dto.turip.TuripPlaceResponse
import com.on.turip.core.network.dto.turip.TuripPostRequest
import com.on.turip.core.network.dto.turip.TuripResponse
import com.on.turip.core.network.service.TuripService

class DefaultTuripDatasource(
    private val turipService: TuripService,
) : TuripDatasource {
    override suspend fun getTurip(turipId: Long): TuripResponse =
        turipService.getTurip(turipId)

    override suspend fun getTurips(): List<TuripResponse> =
        turipService.getTurips().turipsResponse

    override suspend fun getTuripMembers(turipId: Long): List<TuripMemberResponse> =
        turipService.getTuripMembers(turipId).members

    override suspend fun createTurip(name: String): TuripCreationResponse =
        turipService.postTurip(TuripPostRequest(name))

    override suspend fun renameTurip(turipId: Long, name: String) =
        turipService.patchTurip(turipId, TuripPatchRequest(name))

    override suspend fun deleteTurip(turipId: Long) =
        turipService.deleteTurip(turipId)

    override suspend fun exitTurip(turipId: Long) =
        turipService.exitTurip(turipId)

    override suspend fun getTuripsByPlaceId(placeId: Long): List<TuripByPlaceResponse> =
        turipService.getTuripsByPlaceId(placeId).turips

    override suspend fun getTuripPlaces(turipId: Long): List<TuripPlaceResponse> =
        turipService.getTuripPlaces(turipId).turipPlaceResponses

    override suspend fun addTuripPlace(turipId: Long, placeId: Long) =
        turipService.postTuripPlace(turipId, placeId)

    override suspend fun removeTuripPlace(turipId: Long, placeId: Long) =
        turipService.deleteTuripPlace(turipId, placeId)

    override suspend fun reorderTuripPlaces(turipId: Long, placeIds: List<Long>) =
        turipService.patchTuripPlaceOrder(turipId, TuripPlaceOrderRequest(placeIds))

    override suspend fun updatePlaceTurips(placeId: Long, turipIds: List<Long>) =
        turipService.putPlaceTurips(placeId, PlaceTuripsRequest(turipIds))

    override suspend fun generateInvitationToken(turipId: Long): TuripInvitationTokenResponse =
        turipService.postInvitationToken(turipId)

    override suspend fun getInvitationInformation(token: String): TuripInvitationInformationResponse =
        turipService.getInvitationInformation(token)

    override suspend fun joinTurip(turipId: Long) {
        turipService.postJoinTurip(turipId)
    }
}
