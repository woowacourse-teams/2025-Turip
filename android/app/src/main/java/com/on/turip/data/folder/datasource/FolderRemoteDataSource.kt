package com.on.turip.data.folder.datasource

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.folder.dto.FavoriteFolderAddRequest
import com.on.turip.data.folder.dto.FavoriteFolderPatchRequest
import com.on.turip.data.folder.dto.FavoriteFoldersResponse

interface FolderRemoteDataSource {
    suspend fun getFavoriteFolders(): TuripCustomResult<FavoriteFoldersResponse>

    suspend fun postFavoriteFolder(favoriteFolderAddRequest: FavoriteFolderAddRequest): TuripCustomResult<Unit>

    suspend fun patchFavoriteFolder(
        folderId: Long,
        favoriteFolderPatchRequest: FavoriteFolderPatchRequest,
    ): TuripCustomResult<Unit>

    suspend fun deleteFavoriteFolder(folderId: Long): TuripCustomResult<Unit>
}
