package com.on.turip.data.place.repository

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.common.mapCatching
import com.on.turip.data.place.datasource.FavoritePlaceRemoteDataSource
import com.on.turip.data.place.toDomain
import com.on.turip.domain.favorite.FavoritePlace
import com.on.turip.domain.favorite.repository.FavoritePlaceRepository
import com.on.turip.domain.trip.Place

class DefaultFavoritePlaceRepository(
    private val favoritePlaceRemoteDataSource: FavoritePlaceRemoteDataSource,
) : FavoritePlaceRepository {
    override suspend fun loadFavoritePlaces(favoriteFolderId: Long): TuripCustomResult<List<FavoritePlace>> =
        favoritePlaceRemoteDataSource.getFavoritePlaces(favoriteFolderId).mapCatching {
            it.toDomain()
        }

    override suspend fun createFavoritePlace(
        favoriteFolderId: Long,
        placeId: Long,
    ): TuripCustomResult<Unit> =
        favoritePlaceRemoteDataSource.createFavoritePlace(
            favoriteFolderId = favoriteFolderId,
            placeId = placeId,
        )

    override suspend fun deleteFavoritePlace(
        favoriteFolderId: Long,
        placeId: Long,
    ): TuripCustomResult<Unit> =
        favoritePlaceRemoteDataSource.deleteFavoritePlace(
            favoriteFolderId = favoriteFolderId,
            placeId = placeId,
        )
}
