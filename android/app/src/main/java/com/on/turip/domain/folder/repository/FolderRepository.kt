package com.on.turip.domain.folder.repository

import com.on.turip.core.result.TuripResult
import com.on.turip.domain.folder.FavoriteFolder
import com.on.turip.domain.folder.Folder

interface FolderRepository {
    suspend fun loadFavoriteFolders(): TuripResult<List<Folder>>

    suspend fun createFavoriteFolder(name: String): TuripResult<Folder>

    suspend fun updateFavoriteFolder(
        folderId: Long,
        updateName: String,
    ): TuripResult<Unit>

    suspend fun deleteFavoriteFolder(folderId: Long): TuripResult<Unit>

    suspend fun loadFavoriteFoldersStatusByPlaceId(placeId: Long): TuripResult<List<FavoriteFolder>>
}
