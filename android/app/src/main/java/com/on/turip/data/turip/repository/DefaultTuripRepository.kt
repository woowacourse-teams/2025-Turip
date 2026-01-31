package com.on.turip.data.turip.repository

import com.on.turip.core.result.TuripResult
import com.on.turip.core.result.mapCatching
import com.on.turip.data.turip.datasource.TuripRemoteDataSource
import com.on.turip.data.turip.dto.TuripPlaceOrderRequest
import com.on.turip.data.turip.toDomain
import com.on.turip.data.turip.toPatchRequestDto
import com.on.turip.data.turip.toPostRequestDto
import com.on.turip.domain.favorite.TuripPlace
import com.on.turip.domain.folder.Turip
import com.on.turip.domain.folder.repository.TuripRepository
import javax.inject.Inject

class DefaultTuripRepository @Inject constructor(
    private val turipRemoteDataSource: TuripRemoteDataSource,
) : TuripRepository {
    override suspend fun loadTurips(): TuripResult<List<Turip>> = turipRemoteDataSource.getTurips().mapCatching { it.toDomain() }

    override suspend fun createTurip(name: String): TuripResult<Turip> =
        turipRemoteDataSource
            .postTurip(name.toPostRequestDto())
            .mapCatching { it.toDomain() }

    override suspend fun updateTurip(
        turipId: Long,
        updateName: String,
    ): TuripResult<Unit> = turipRemoteDataSource.patchTurip(turipId, updateName.toPatchRequestDto())

    override suspend fun deleteTurip(turipId: Long): TuripResult<Unit> = turipRemoteDataSource.deleteTurip(turipId)

    override suspend fun loadTuripsByPlaceId(placeId: Long): TuripResult<List<Turip>> =
        turipRemoteDataSource
            .getTuripsByPlaceId(placeId)
            .mapCatching { it.toDomain() }

    override suspend fun loadTuripPlaces(turipId: Long): TuripResult<List<TuripPlace>> =
        turipRemoteDataSource.getTuripPlaces(turipId).mapCatching { it.toDomain() }

    override suspend fun createTuripPlace(
        turipId: Long,
        placeId: Long,
    ): TuripResult<Unit> = turipRemoteDataSource.createTuripPlace(turipId = turipId, placeId = placeId)

    override suspend fun deleteTuripPlace(
        turipId: Long,
        placeId: Long,
    ): TuripResult<Unit> = turipRemoteDataSource.deleteTuripPlace(turipId = turipId, placeId = placeId)

    override suspend fun updateTuripPlacesOrder(
        turipId: Long,
        updatedOrder: List<Long>,
    ): TuripResult<Unit> =
        turipRemoteDataSource
            .patchTuripPlacesOrder(
                turipId = turipId,
                turipPlaceOrderRequest = TuripPlaceOrderRequest(turipPlaceIdsOrder = updatedOrder),
            )
}
