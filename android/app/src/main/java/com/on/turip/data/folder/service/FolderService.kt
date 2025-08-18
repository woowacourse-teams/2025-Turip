package com.on.turip.data.folder.service

import com.on.turip.data.folder.dto.FavoriteFolderAddRequest
import com.on.turip.data.folder.dto.FavoriteFoldersResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FolderService {
    @GET("favorite-folders")
    suspend fun getFavoriteFolders(): Response<FavoriteFoldersResponse>

    @POST("favorite-folders")
    suspend fun postFavoriteFolder(
        @Body favoriteFolderAddRequest: FavoriteFolderAddRequest,
    ): Response<Unit>
}
