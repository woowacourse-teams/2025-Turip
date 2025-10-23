package com.on.turip.data.folder.datasource

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.folder.dto.FavoriteFolderPatchRequest
import com.on.turip.data.folder.dto.FavoriteFolderPostRequest
import com.on.turip.data.folder.dto.FavoriteFolderPostResponse
import com.on.turip.data.folder.dto.FavoriteFoldersResponse
import com.on.turip.data.folder.dto.FavoriteFoldersStatusByPlaceResponse

interface FolderRemoteDataSource {
    suspend fun getFavoriteFolders(): TuripCustomResult<FavoriteFoldersResponse>

    suspend fun postFavoriteFolder(favoriteFolderPostRequest: FavoriteFolderPostRequest): TuripCustomResult<FavoriteFolderPostResponse>

    suspend fun patchFavoriteFolder(
        folderId: Long,
        favoriteFolderPatchRequest: FavoriteFolderPatchRequest,
    ): TuripCustomResult<Unit>

    suspend fun deleteFavoriteFolder(folderId: Long): TuripCustomResult<Unit>

    suspend fun getFavoriteFoldersStatusByPlaceId(placeId: Long): TuripCustomResult<FavoriteFoldersStatusByPlaceResponse>
}
