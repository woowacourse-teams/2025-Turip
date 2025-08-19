package com.on.turip.data.place.datasource

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.common.safeApiCall
import com.on.turip.data.place.dto.FavoritePlacesResponse
import com.on.turip.data.place.service.PlaceService

class DefaultFavoritePlaceRemoteDataSource(
    private val placeService: PlaceService,
) : FavoritePlaceRemoteDataSource {
    override suspend fun getFavoritePlaces(favoriteFolderId: Long): TuripCustomResult<FavoritePlacesResponse> =
        safeApiCall { placeService.getFavoritePlaces(favoriteFolderId) }

    override suspend fun createFavoritePlace(
        favoriteFolderId: Long,
        placeId: Long,
    ): TuripCustomResult<Unit> = safeApiCall { placeService.postFavoritePlace(favoriteFolderId, placeId) }

    override suspend fun deleteFavoritePlace(
        favoriteFolderId: Long,
        placeId: Long,
    ): TuripCustomResult<Unit> = safeApiCall { placeService.deleteFavoritePlace(favoriteFolderId, placeId) }
}
