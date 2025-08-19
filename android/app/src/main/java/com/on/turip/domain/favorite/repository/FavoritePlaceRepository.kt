package com.on.turip.domain.favorite.repository

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.domain.trip.Place

interface FavoritePlaceRepository {
    suspend fun loadFavoritePlaces(favoriteFolderId: Long): TuripCustomResult<List<Place>>

    suspend fun createFavoritePlace(
        favoriteFolderId: Long,
        placeId: Long,
    ): TuripCustomResult<Unit>
}
