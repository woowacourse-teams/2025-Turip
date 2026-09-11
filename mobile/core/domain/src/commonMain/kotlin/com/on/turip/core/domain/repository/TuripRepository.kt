package com.on.turip.core.domain.repository

import com.on.turip.core.model.bookmark.TuripPlace
import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.model.turip.Turip
import com.on.turip.core.model.turip.TuripInvitationInformation
import com.on.turip.core.model.turip.TuripInvitationToken
import com.on.turip.core.model.turip.TuripMember
import com.on.turip.core.model.turip.result.TuripStreamResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface TuripRepository {
    val turips: StateFlow<List<Turip>>

    suspend fun loadTurip(turipId: Long): TuripResult<Turip>

    suspend fun loadTurips(): TuripResult<List<Turip>>

    suspend fun loadTuripMembers(turipId: Long): TuripResult<List<TuripMember>>

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

    /**
     * 여러 장소를 한 번에 담는다.
     *
     * @return 실제로 담긴 장소의 id. 이미 담겨 있던 장소는 서버 응답에서 빠지므로 요청보다 적을 수 있다.
     */
    suspend fun createTuripPlaces(
        turipId: Long,
        placeIds: List<Long>,
    ): TuripResult<List<Long>>

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
        previouslySelectedIds: Set<Long>,
    ): TuripResult<Unit>

    suspend fun createInvitationToken(turipId: Long): TuripResult<TuripInvitationToken>

    suspend fun joinTurip(turipId: Long): TuripResult<Unit>

    suspend fun verifyInvitationToken(token: String): TuripResult<TuripInvitationInformation>

    fun streamTuripEvents(turipId: Long): Flow<TuripStreamResult>

    fun updateCachedTuripMemberCount(
        turipId: Long,
        memberCount: Int,
    )

    fun updateCachedTuripSharedStatus(
        turipId: Long,
        isShared: Boolean,
    )

    fun clearCache()
}
