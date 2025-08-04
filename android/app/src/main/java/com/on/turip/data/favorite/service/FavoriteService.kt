package com.on.turip.data.favorite.service

import com.on.turip.data.favorite.dto.FavoriteAddRequest
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface FavoriteService {
    @POST("favorites")
    suspend fun postFavorite(
        @Header("device-fid") fid: String,
        @Body favoriteAddRequest: FavoriteAddRequest,
    )
}
