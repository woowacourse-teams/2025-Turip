package com.on.turip.domain.folder.repository

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.domain.folder.FavoriteFolder
import com.on.turip.domain.folder.Folder

interface FolderRepository {
    suspend fun loadFavoriteFolders(): TuripCustomResult<List<Folder>>

    suspend fun createFavoriteFolder(name: String): TuripCustomResult<Folder>

    suspend fun updateFavoriteFolder(
        folderId: Long,
        updateName: String,
    ): TuripCustomResult<Unit>

    suspend fun deleteFavoriteFolder(folderId: Long): TuripCustomResult<Unit>

    suspend fun loadFavoriteFoldersStatusByPlaceId(placeId: Long): TuripCustomResult<List<FavoriteFolder>>
}
