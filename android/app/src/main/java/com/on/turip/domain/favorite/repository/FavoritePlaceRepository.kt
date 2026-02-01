package com.on.turip.domain.favorite.repository

import com.on.turip.core.result.TuripResult
import com.on.turip.domain.favorite.TuripPlace

interface FavoritePlaceRepository {
    suspend fun loadTuripPlaces(favoriteFolderId: Long): TuripResult<List<TuripPlace>>

    suspend fun createFavoritePlace(
        favoriteFolderId: Long,
        placeId: Long,
    ): TuripResult<Unit>

    suspend fun deleteFavoritePlace(
        favoriteFolderId: Long,
        placeId: Long,
    ): TuripResult<Unit>

    suspend fun updateTuripPlacesOrder(
        turipId: Long,
        updatedOrder: List<Long>,
    ): TuripResult<Unit>
}
