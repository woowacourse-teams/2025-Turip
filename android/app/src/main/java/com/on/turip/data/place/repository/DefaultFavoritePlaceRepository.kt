package com.on.turip.data.place.repository

import com.on.turip.core.result.TuripResult
import com.on.turip.core.result.mapCatching
import com.on.turip.data.place.datasource.FavoritePlaceRemoteDataSource
import com.on.turip.data.place.dto.FavoritePlaceOrderRequest
import com.on.turip.data.place.toDomain
import com.on.turip.domain.favorite.TuripPlace
import com.on.turip.domain.favorite.repository.FavoritePlaceRepository
import javax.inject.Inject

class DefaultFavoritePlaceRepository @Inject constructor(
    private val favoritePlaceRemoteDataSource: FavoritePlaceRemoteDataSource,
) : FavoritePlaceRepository {
    override suspend fun loadTuripPlaces(favoriteFolderId: Long): TuripResult<List<TuripPlace>> =
        favoritePlaceRemoteDataSource.getFavoritePlaces(favoriteFolderId).mapCatching {
            it.toDomain()
        }

    override suspend fun createFavoritePlace(
        favoriteFolderId: Long,
        placeId: Long,
    ): TuripResult<Unit> =
        favoritePlaceRemoteDataSource.createFavoritePlace(
            favoriteFolderId = favoriteFolderId,
            placeId = placeId,
        )

    override suspend fun deleteFavoritePlace(
        favoriteFolderId: Long,
        placeId: Long,
    ): TuripResult<Unit> =
        favoritePlaceRemoteDataSource.deleteFavoritePlace(
            favoriteFolderId = favoriteFolderId,
            placeId = placeId,
        )

    override suspend fun updateTuripPlacesOrder(
        turipId: Long,
        updatedOrder: List<Long>,
    ): TuripResult<Unit> =
        favoritePlaceRemoteDataSource.patchFavoritePlacesOrder(
            favoriteFolderId = turipId,
            favoritePlaceOrderRequest =
                FavoritePlaceOrderRequest(
                    favoritePlaceIdsOrder = updatedOrder,
                ),
        )
}
