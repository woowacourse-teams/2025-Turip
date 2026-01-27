package com.on.turip.data.place.repository

import com.on.turip.core.result.TuripResult
import com.on.turip.core.result.mapCatching
import com.on.turip.data.place.datasource.TuripPlaceRemoteDataSource
import com.on.turip.data.place.dto.TuripPlaceOrderRequest
import com.on.turip.data.place.toDomain
import com.on.turip.domain.favorite.TuripPlace
import com.on.turip.domain.favorite.repository.TuripPlaceRepository
import javax.inject.Inject

class DefaultTuripPlaceRepository @Inject constructor(
    private val turipPlaceRemoteDataSource: TuripPlaceRemoteDataSource,
) : TuripPlaceRepository {
    override suspend fun loadTuripPlaces(turipId: Long): TuripResult<List<TuripPlace>> =
        turipPlaceRemoteDataSource.getTuripPlaces(turipId).mapCatching { it.toDomain() }

    override suspend fun createTuripPlace(
        turipId: Long,
        placeId: Long,
    ): TuripResult<Unit> = turipPlaceRemoteDataSource.createTuripPlace(turipId = turipId, placeId = placeId)

    override suspend fun deleteTuripPlace(
        turipId: Long,
        placeId: Long,
    ): TuripResult<Unit> = turipPlaceRemoteDataSource.deleteTuripPlace(turipId = turipId, placeId = placeId)

    override suspend fun updateTuripPlacesOrder(
        turipId: Long,
        updatedOrder: List<Long>,
    ): TuripResult<Unit> =
        turipPlaceRemoteDataSource
            .patchTuripPlacesOrder(
                turipId = turipId,
                turipPlaceOrderRequest = TuripPlaceOrderRequest(turipPlaceIdsOrder = updatedOrder),
            )
}
