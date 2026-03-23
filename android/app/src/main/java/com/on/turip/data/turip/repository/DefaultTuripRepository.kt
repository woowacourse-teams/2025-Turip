package com.on.turip.data.turip.repository

import com.on.turip.core.result.ErrorType
import com.on.turip.core.result.TuripResult
import com.on.turip.core.result.mapCatching
import com.on.turip.data.result.toErrorType
import com.on.turip.data.turip.datasource.TuripRemoteDataSource
import com.on.turip.data.turip.datasource.TuripSseStreamDataSource
import com.on.turip.data.turip.dto.PlaceTuripsRequest
import com.on.turip.data.turip.dto.TuripPlaceOrderRequest
import com.on.turip.data.turip.toDomain
import com.on.turip.data.turip.toPatchRequestDto
import com.on.turip.data.turip.toPostRequestDto
import com.on.turip.domain.bookmark.TuripPlace
import com.on.turip.domain.turip.Turip
import com.on.turip.domain.turip.TuripInvitationInformation
import com.on.turip.domain.turip.TuripInvitationToken
import com.on.turip.domain.turip.TuripMember
import com.on.turip.domain.turip.TuripStreamEvent
import com.on.turip.domain.turip.repository.TuripRepository
import com.on.turip.domain.turip.result.TuripStreamResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultTuripRepository @Inject constructor(
    private val turipRestRemoteDataSource: TuripRemoteDataSource,
    private val turipSseStreamDataSource: TuripSseStreamDataSource,
) : TuripRepository {
    override suspend fun loadTurip(turipId: Long): TuripResult<Turip> =
        turipRestRemoteDataSource.getTurip(turipId).mapCatching { it.toDomain() }

    override suspend fun loadTurips(): TuripResult<List<Turip>> = turipRestRemoteDataSource.getTurips().mapCatching { it.toDomain() }

    override suspend fun loadTuripMembers(turipId: Long): TuripResult<List<TuripMember>> =
        turipRestRemoteDataSource.getTuripMembers(turipId).mapCatching { it.toDomain() }

    override suspend fun createTurip(name: String): TuripResult<Turip> =
        turipRestRemoteDataSource
            .postTurip(name.toPostRequestDto())
            .mapCatching { it.toDomain() }

    override suspend fun updateTurip(
        turipId: Long,
        updateName: String,
    ): TuripResult<Unit> = turipRestRemoteDataSource.patchTurip(turipId, updateName.toPatchRequestDto())

    override suspend fun deleteTurip(turipId: Long): TuripResult<Unit> = turipRestRemoteDataSource.deleteTurip(turipId)

    override suspend fun exitTurip(turipId: Long): TuripResult<Unit> = turipRestRemoteDataSource.exitTurip(turipId)

    override suspend fun loadTuripsByPlaceId(placeId: Long): TuripResult<List<Turip>> =
        turipRestRemoteDataSource
            .getTuripsByPlaceId(placeId)
            .mapCatching { it.toDomain() }

    override suspend fun loadTuripPlaces(turipId: Long): TuripResult<List<TuripPlace>> =
        turipRestRemoteDataSource.getTuripPlaces(turipId).mapCatching { it.toDomain() }

    override suspend fun createTuripPlace(
        turipId: Long,
        placeId: Long,
    ): TuripResult<Unit> = turipRestRemoteDataSource.createTuripPlace(turipId = turipId, placeId = placeId)

    override suspend fun deleteTuripPlace(
        turipId: Long,
        placeId: Long,
    ): TuripResult<Unit> = turipRestRemoteDataSource.deleteTuripPlace(turipId = turipId, placeId = placeId)

    override suspend fun updateTuripPlacesOrder(
        turipId: Long,
        updatedOrder: List<Long>,
    ): TuripResult<Unit> =
        turipRestRemoteDataSource
            .patchTuripPlacesOrder(
                turipId = turipId,
                turipPlaceOrderRequest = TuripPlaceOrderRequest(turipPlaceIdsOrder = updatedOrder),
            )

    override suspend fun updatePlaceTurips(
        placeId: Long,
        turipIds: List<Long>,
    ): TuripResult<Unit> =
        turipRestRemoteDataSource.putPlaceTurips(
            placeId = placeId,
            placeTuripsRequest = PlaceTuripsRequest(turipIds),
        )

    override suspend fun createInvitationToken(turipId: Long): TuripResult<TuripInvitationToken> =
        turipRestRemoteDataSource.createInvitationToken(turipId).mapCatching { it.toDomain() }

    override suspend fun joinTurip(turipId: Long): TuripResult<Unit> = turipRestRemoteDataSource.joinTurip(turipId).mapCatching { Unit }

    override suspend fun verifyInvitationToken(token: String): TuripResult<TuripInvitationInformation> =
        turipRestRemoteDataSource.getInvitationInformation(token).mapCatching { it.toDomain() }

    override fun streamTuripEvents(turipId: Long): Flow<TuripStreamResult> =
        turipSseStreamDataSource
            .streamTuripEvents(turipId)
            .map<TuripStreamEvent, TuripStreamResult> { event ->
                TuripStreamResult.Event(event)
            }.catch { throwable ->
                val fatalResult =
                    when (val errorType = throwable.toErrorType()) {
                        ErrorType.Auth.TokenExpired -> TuripStreamResult.Fatal.TokenExpired
                        ErrorType.Auth.Forbidden -> TuripStreamResult.Fatal.Forbidden
                        else -> TuripStreamResult.Fatal.ConnectionLost(errorType)
                    }

                emit(fatalResult)
            }
}
