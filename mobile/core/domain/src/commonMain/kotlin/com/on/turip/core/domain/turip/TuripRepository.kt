package com.on.turip.core.domain.turip

import com.on.turip.core.model.Member
import com.on.turip.core.model.Turip
import com.on.turip.core.model.TuripInvitationInformation
import com.on.turip.core.model.TuripPlace
import com.on.turip.core.model.TuripStatus

interface TuripRepository {
    suspend fun getTurips(): Result<List<Turip>>

    suspend fun getTurip(turipId: Long): Result<Turip>

    suspend fun getTuripMembers(turipId: Long): Result<List<Member>>

    suspend fun createTurip(name: String): Result<Turip>

    suspend fun renameTurip(turipId: Long, name: String): Result<Unit>

    suspend fun deleteTurip(turipId: Long): Result<Unit>

    suspend fun exitTurip(turipId: Long): Result<Unit>

    suspend fun getTuripsByPlaceId(placeId: Long): Result<List<TuripStatus>>

    suspend fun getTuripPlaces(turipId: Long): Result<List<TuripPlace>>

    suspend fun addTuripPlace(turipId: Long, placeId: Long): Result<Unit>

    suspend fun removeTuripPlace(turipId: Long, placeId: Long): Result<Unit>

    suspend fun reorderTuripPlaces(turipId: Long, placeIds: List<Long>): Result<Unit>

    suspend fun updatePlaceTurips(placeId: Long, turipIds: List<Long>): Result<Unit>

    suspend fun generateInvitationToken(turipId: Long): Result<String>

    suspend fun getInvitationInformation(token: String): Result<TuripInvitationInformation>

    suspend fun joinTurip(turipId: Long): Result<Unit>
}
