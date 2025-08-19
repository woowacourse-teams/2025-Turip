package com.on.turip.data.place.repository

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.common.mapCatching
import com.on.turip.data.place.datasource.FavoritePlaceDataSource
import com.on.turip.data.place.toDomain
import com.on.turip.domain.favorite.repository.FavoritePlaceRepository
import com.on.turip.domain.trip.Place

class DefaultFavoritePlaceRepository(
    private val favoritePlaceDataSource: FavoritePlaceDataSource,
) : FavoritePlaceRepository {
    override suspend fun loadFavoritePlaces(favoriteFolderId: Long): TuripCustomResult<List<Place>> =
        favoritePlaceDataSource.getFavoritePlaces(favoriteFolderId).mapCatching {
            it.toDomain()
        }
}
