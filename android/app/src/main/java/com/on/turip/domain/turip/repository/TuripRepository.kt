package com.on.turip.domain.turip.repository

import com.on.turip.core.result.TuripResult
import com.on.turip.domain.bookmark.TuripPlace
import com.on.turip.domain.turip.Turip
import com.on.turip.domain.turip.TuripInvitationInformation
import com.on.turip.domain.turip.TuripInvitationToken
import com.on.turip.domain.turip.TuripStreamEvent
import kotlinx.coroutines.flow.Flow

interface TuripRepository {
    suspend fun loadTurip(turipId: Long): TuripResult<Turip>

    suspend fun loadTurips(): TuripResult<List<Turip>>

    suspend fun loadTuripMembers(turipId: Long): TuripResult<List<String>>

    suspend fun createTurip(name: String): TuripResult<Turip>

    suspend fun updateTurip(
        turipId: Long,
        updateName: String,
    ): TuripResult<Unit>

    suspend fun deleteTurip(turipId: Long): TuripResult<Unit>

    suspend fun exitTurip(turipId: Long): TuripResult<Unit>

    suspend fun loadTuripsByPlaceId(placeId: Long): TuripResult<List<Turip>>

    suspend fun loadTuripPlaces(turipId: Long): TuripResult<List<TuripPlace>>

    suspend fun createTuripPlace(
        turipId: Long,
        placeId: Long,
    ): TuripResult<Unit>

    suspend fun deleteTuripPlace(
        turipId: Long,
        placeId: Long,
    ): TuripResult<Unit>

    suspend fun updateTuripPlacesOrder(
        turipId: Long,
        updatedOrder: List<Long>,
    ): TuripResult<Unit>

    suspend fun updatePlaceTurips(
        placeId: Long,
        turipIds: List<Long>,
    ): TuripResult<Unit>

    suspend fun createInvitationToken(turipId: Long): TuripResult<TuripInvitationToken>

    suspend fun joinTurip(turipId: Long): TuripResult<Unit>

    suspend fun verifyInvitationToken(token: String): TuripResult<TuripInvitationInformation>

    fun streamTuripEvents(turipId: Long): Flow<TuripResult<TuripStreamEvent>>
}
