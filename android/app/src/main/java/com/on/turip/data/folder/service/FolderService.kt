package com.on.turip.data.folder.service

import com.on.turip.data.folder.dto.FavoriteFoldersResponse
import retrofit2.Response
import retrofit2.http.GET

interface FolderService {
    @GET("/favorite-folders")
    suspend fun getFavoriteFolders(): Response<FavoriteFoldersResponse>
}
