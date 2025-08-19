package com.on.turip.data.place.datasource

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.place.dto.FavoritePlacesResponse

interface FavoritePlaceDataSource {
    suspend fun getFavoritePlaces(favoriteFolderId: Long): TuripCustomResult<FavoritePlacesResponse>

    suspend fun createFavoritePlace(
        favoritePlaceId: Long,
        placeId: Long,
    ): TuripCustomResult<Unit>
}
