package com.on.turip.data.place.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.data.result.safeApiCall
import com.on.turip.data.place.dto.FavoritePlaceOrderRequest
import com.on.turip.data.place.dto.FavoritePlacesResponse
import com.on.turip.data.place.service.PlaceService
import javax.inject.Inject

class DefaultFavoritePlaceRemoteDataSource @Inject constructor(
    private val placeService: PlaceService,
) : FavoritePlaceRemoteDataSource {
    override suspend fun getFavoritePlaces(favoriteFolderId: Long): TuripResult<FavoritePlacesResponse> =
        safeApiCall { placeService.getFavoritePlaces(favoriteFolderId) }

    override suspend fun createFavoritePlace(
        favoriteFolderId: Long,
        placeId: Long,
    ): TuripResult<Unit> = safeApiCall { placeService.postFavoritePlace(favoriteFolderId, placeId) }

    override suspend fun deleteFavoritePlace(
        favoriteFolderId: Long,
        placeId: Long,
    ): TuripResult<Unit> =
        safeApiCall { placeService.deleteFavoritePlace(favoriteFolderId, placeId) }

    override suspend fun patchFavoritePlacesOrder(
        favoriteFolderId: Long,
        favoritePlaceOrderRequest: FavoritePlaceOrderRequest,
    ): TuripResult<Unit> =
        safeApiCall {
            placeService.patchFavoritePlaceOrder(
                favoriteFolderId,
                favoritePlaceOrderRequest,
            )
        }
}
