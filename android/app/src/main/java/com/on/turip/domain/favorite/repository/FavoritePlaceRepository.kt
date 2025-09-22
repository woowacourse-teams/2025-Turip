package com.on.turip.domain.favorite.repository

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.domain.favorite.FavoritePlace

interface FavoritePlaceRepository {
    suspend fun loadFavoritePlaces(favoriteFolderId: Long): TuripCustomResult<List<FavoritePlace>>

    suspend fun createFavoritePlace(
        favoriteFolderId: Long,
        placeId: Long,
    ): TuripCustomResult<Unit>

    suspend fun deleteFavoritePlace(
        favoriteFolderId: Long,
        placeId: Long,
    ): TuripCustomResult<Unit>

    suspend fun updateFavoritePlacesOrder(
        favoriteFolderId: Long,
        updatedOrder: List<Long>,
    )
}
