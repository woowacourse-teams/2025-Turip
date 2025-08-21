package com.on.turip.data.folder.service

import com.on.turip.data.folder.dto.FavoriteFolderAddRequest
import com.on.turip.data.folder.dto.FavoriteFolderPatchRequest
import com.on.turip.data.folder.dto.FavoriteFoldersResponse
import com.on.turip.data.folder.dto.FavoriteFoldersStatusByPlaceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FolderService {
    @GET("favorite-folders")
    suspend fun getFavoriteFolders(): Response<FavoriteFoldersResponse>

    @POST("favorite-folders")
    suspend fun postFavoriteFolder(
        @Body favoriteFolderAddRequest: FavoriteFolderAddRequest,
    ): Response<Unit>

    @PATCH("favorite-folders/{favoriteFolderId}")
    suspend fun patchFavoriteFolder(
        @Path("favoriteFolderId") folderId: Long,
        @Body favoriteFolderPatchRequest: FavoriteFolderPatchRequest,
    ): Response<Unit>

    @DELETE("favorite-folders/{favoriteFolderId}")
    suspend fun deleteFavoriteFolder(
        @Path("favoriteFolderId") folderId: Long,
    ): Response<Unit>

    @GET("favorite-folders/favorite-status")
    suspend fun getFavoriteFoldersStatusByPlaceId(
        @Query("placeId") placeId: Long,
    ): Response<FavoriteFoldersStatusByPlaceResponse>
}
