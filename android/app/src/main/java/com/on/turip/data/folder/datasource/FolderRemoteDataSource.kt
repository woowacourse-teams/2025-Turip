package com.on.turip.data.folder.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.data.folder.dto.FavoriteFolderCreationResponse
import com.on.turip.data.folder.dto.FavoriteFolderPatchRequest
import com.on.turip.data.folder.dto.FavoriteFolderPostRequest
import com.on.turip.data.folder.dto.FavoriteFoldersResponse
import com.on.turip.data.folder.dto.FavoriteFoldersStatusByPlaceResponse

interface FolderRemoteDataSource {
    suspend fun getFavoriteFolders(): TuripResult<FavoriteFoldersResponse>

    suspend fun postFavoriteFolder(favoriteFolderPostRequest: FavoriteFolderPostRequest): TuripResult<FavoriteFolderCreationResponse>

    suspend fun patchFavoriteFolder(
        folderId: Long,
        favoriteFolderPatchRequest: FavoriteFolderPatchRequest,
    ): TuripResult<Unit>

    suspend fun deleteFavoriteFolder(folderId: Long): TuripResult<Unit>

    suspend fun getFavoriteFoldersStatusByPlaceId(placeId: Long): TuripResult<FavoriteFoldersStatusByPlaceResponse>
}
