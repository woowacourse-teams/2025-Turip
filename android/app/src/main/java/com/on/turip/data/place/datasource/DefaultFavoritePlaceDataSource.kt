package com.on.turip.data.place.datasource

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.common.safeApiCall
import com.on.turip.data.place.dto.FavoritePlacesResponse
import com.on.turip.data.place.service.PlaceService

class DefaultFavoritePlaceDataSource(
    private val placeService: PlaceService,
) : FavoritePlaceDataSource {
    override suspend fun getFavoritePlaces(favoriteFolderId: Long): TuripCustomResult<FavoritePlacesResponse> =
        safeApiCall { placeService.getFavoritePlaces(favoriteFolderId) }
}
