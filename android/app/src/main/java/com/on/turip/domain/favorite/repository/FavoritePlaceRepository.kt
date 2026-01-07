package com.on.turip.domain.favorite.repository

import com.on.turip.core.result.TuripResult
import com.on.turip.domain.favorite.FavoritePlace

interface FavoritePlaceRepository {
    suspend fun loadFavoritePlaces(favoriteFolderId: Long): TuripResult<List<FavoritePlace>>

    suspend fun createFavoritePlace(
        favoriteFolderId: Long,
        placeId: Long,
    ): TuripResult<Unit>

    suspend fun deleteFavoritePlace(
        favoriteFolderId: Long,
        placeId: Long,
    ): TuripResult<Unit>

    suspend fun updateFavoritePlacesOrder(
        favoriteFolderId: Long,
        updatedOrder: List<Long>,
    ): TuripResult<Unit>
}
