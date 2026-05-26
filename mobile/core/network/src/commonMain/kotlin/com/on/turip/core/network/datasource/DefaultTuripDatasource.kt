package com.on.turip.core.network.datasource

import com.on.turip.core.network.dto.turip.AddTuripPlaceRequest
import com.on.turip.core.network.dto.turip.CreateTuripRequest
import com.on.turip.core.network.dto.turip.InvitationInformationResponse
import com.on.turip.core.network.dto.turip.InvitationTokenResponse
import com.on.turip.core.network.dto.turip.MemberResponse
import com.on.turip.core.network.dto.turip.RenameTuripRequest
import com.on.turip.core.network.dto.turip.ReorderTuripPlacesRequest
import com.on.turip.core.network.dto.turip.TuripPlaceResponse
import com.on.turip.core.network.dto.turip.TuripResponse
import com.on.turip.core.network.dto.turip.TuripStatusResponse
import com.on.turip.core.network.dto.turip.UpdatePlaceTuripsRequest
import com.on.turip.core.network.service.TuripService

class DefaultTuripDatasource(
    private val turipService: TuripService,
) : TuripDatasource {
    override suspend fun getTurip(turipId: Long): TuripResponse =
        turipService.getTurip(turipId)

    override suspend fun getTurips(): List<TuripResponse> =
        turipService.getTurips()

    override suspend fun getTuripMembers(turipId: Long): List<MemberResponse> =
        turipService.getTuripMembers(turipId)

    override suspend fun createTurip(name: String): TuripResponse =
        turipService.postTurip(CreateTuripRequest(name))

    override suspend fun renameTurip(turipId: Long, name: String) =
        turipService.patchTurip(turipId, RenameTuripRequest(name))

    override suspend fun deleteTurip(turipId: Long) =
        turipService.deleteTurip(turipId)

    override suspend fun exitTurip(turipId: Long) =
        turipService.exitTurip(turipId)

    override suspend fun getTuripsByPlaceId(placeId: Long): List<TuripStatusResponse> =
        turipService.getTuripsByPlaceId(placeId)

    override suspend fun getTuripPlaces(turipId: Long): List<TuripPlaceResponse> =
        turipService.getTuripPlaces(turipId)

    override suspend fun addTuripPlace(turipId: Long, placeId: Long) =
        turipService.postTuripPlace(AddTuripPlaceRequest(turipId, placeId))

    override suspend fun removeTuripPlace(turipId: Long, placeId: Long) =
        turipService.deleteTuripPlace(turipId, placeId)

    override suspend fun reorderTuripPlaces(turipId: Long, placeIds: List<Long>) =
        turipService.patchTuripPlaceOrder(ReorderTuripPlacesRequest(turipId, placeIds))

    override suspend fun updatePlaceTurips(placeId: Long, turipIds: List<Long>) =
        turipService.putPlaceTurips(placeId, UpdatePlaceTuripsRequest(turipIds))

    override suspend fun generateInvitationToken(turipId: Long): InvitationTokenResponse =
        turipService.postInvitationToken(turipId)

    override suspend fun getInvitationInformation(token: String): InvitationInformationResponse =
        turipService.getInvitationInformation(token)

    override suspend fun joinTurip(turipId: Long) =
        turipService.postJoinTurip(turipId)
}
