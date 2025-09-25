package com.on.turip.data.place.datasource

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.place.dto.FavoritePlaceOrderRequest
import com.on.turip.data.place.dto.FavoritePlacesResponse

interface FavoritePlaceRemoteDataSource {
    suspend fun getFavoritePlaces(favoriteFolderId: Long): TuripCustomResult<FavoritePlacesResponse>

    suspend fun createFavoritePlace(
        favoriteFolderId: Long,
        placeId: Long,
    ): TuripCustomResult<Unit>

    suspend fun deleteFavoritePlace(
        favoriteFolderId: Long,
        placeId: Long,
    ): TuripCustomResult<Unit>

    suspend fun patchFavoritePlacesOrder(
        favoriteFolderId: Long,
        favoritePlaceOrderRequest: FavoritePlaceOrderRequest,
    ): TuripCustomResult<Unit>
}
