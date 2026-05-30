package com.on.turip.core.network.datasource

import com.on.turip.core.network.dto.turip.TuripByPlaceResponse
import com.on.turip.core.network.dto.turip.TuripCreationResponse
import com.on.turip.core.network.dto.turip.TuripInvitationInformationResponse
import com.on.turip.core.network.dto.turip.TuripInvitationTokenResponse
import com.on.turip.core.network.dto.turip.TuripMemberResponse
import com.on.turip.core.network.dto.turip.TuripPlaceResponse
import com.on.turip.core.network.dto.turip.TuripResponse

interface TuripDatasource {
    suspend fun getTurip(turipId: Long): TuripResponse

    suspend fun getTurips(): List<TuripResponse>

    suspend fun getTuripMembers(turipId: Long): List<TuripMemberResponse>

    suspend fun createTurip(name: String): TuripCreationResponse

    suspend fun renameTurip(turipId: Long, name: String)

    suspend fun deleteTurip(turipId: Long)

    suspend fun exitTurip(turipId: Long)

    suspend fun getTuripsByPlaceId(placeId: Long): List<TuripByPlaceResponse>

    suspend fun getTuripPlaces(turipId: Long): List<TuripPlaceResponse>

    suspend fun addTuripPlace(turipId: Long, placeId: Long)

    suspend fun removeTuripPlace(turipId: Long, placeId: Long)

    suspend fun reorderTuripPlaces(turipId: Long, placeIds: List<Long>)

    suspend fun updatePlaceTurips(placeId: Long, turipIds: List<Long>)

    suspend fun generateInvitationToken(turipId: Long): TuripInvitationTokenResponse

    suspend fun getInvitationInformation(token: String): TuripInvitationInformationResponse

    suspend fun joinTurip(turipId: Long)
}
