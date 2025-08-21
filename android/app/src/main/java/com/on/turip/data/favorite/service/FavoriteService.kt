package com.on.turip.data.favorite.service

import com.on.turip.data.favorite.dto.FavoriteAddRequest
import com.on.turip.data.favorite.dto.FavoriteContentsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface FavoriteService {
    @POST("favorite-contents")
    suspend fun postFavorite(
        @Body favoriteAddRequest: FavoriteAddRequest,
    ): Response<Unit>

    @DELETE("favorite-contents")
    suspend fun deleteFavorite(
        @Query("contentId") contentId: Long,
    ): Response<Unit>

    @GET("favorite-contents")
    suspend fun getFavoriteContents(
        @Query("size") size: Int,
        @Query("lastId") lastId: Long,
    ): Response<FavoriteContentsResponse>
}
