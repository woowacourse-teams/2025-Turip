package com.on.turip.core.network.datasource

import com.on.turip.core.network.dto.turip.InvitationInformationResponse
import com.on.turip.core.network.dto.turip.InvitationTokenResponse
import com.on.turip.core.network.dto.turip.MemberResponse
import com.on.turip.core.network.dto.turip.TuripPlaceResponse
import com.on.turip.core.network.dto.turip.TuripResponse
import com.on.turip.core.network.dto.turip.TuripStatusResponse

interface TuripDatasource {
    suspend fun getTurip(turipId: Long): TuripResponse
    suspend fun getTurips(): List<TuripResponse>
    suspend fun getTuripMembers(turipId: Long): List<MemberResponse>
    suspend fun createTurip(name: String): TuripResponse
    suspend fun renameTurip(turipId: Long, name: String)
    suspend fun deleteTurip(turipId: Long)
    suspend fun exitTurip(turipId: Long)
    suspend fun getTuripsByPlaceId(placeId: Long): List<TuripStatusResponse>
    suspend fun getTuripPlaces(turipId: Long): List<TuripPlaceResponse>
    suspend fun addTuripPlace(turipId: Long, placeId: Long)
    suspend fun removeTuripPlace(turipId: Long, placeId: Long)
    suspend fun reorderTuripPlaces(turipId: Long, placeIds: List<Long>)
    suspend fun updatePlaceTurips(placeId: Long, turipIds: List<Long>)
    suspend fun generateInvitationToken(turipId: Long): InvitationTokenResponse
    suspend fun getInvitationInformation(token: String): InvitationInformationResponse
    suspend fun joinTurip(turipId: Long)
}
