package com.on.turip.data.place.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.data.place.dto.FavoritePlaceOrderRequest
import com.on.turip.data.place.dto.FavoritePlacesResponse

interface FavoritePlaceRemoteDataSource {
    suspend fun getFavoritePlaces(favoriteFolderId: Long): TuripResult<FavoritePlacesResponse>

    suspend fun createFavoritePlace(
        favoriteFolderId: Long,
        placeId: Long,
    ): TuripResult<Unit>

    suspend fun deleteFavoritePlace(
        favoriteFolderId: Long,
        placeId: Long,
    ): TuripResult<Unit>

    suspend fun patchFavoritePlacesOrder(
        favoriteFolderId: Long,
        favoritePlaceOrderRequest: FavoritePlaceOrderRequest,
    ): TuripResult<Unit>
}
