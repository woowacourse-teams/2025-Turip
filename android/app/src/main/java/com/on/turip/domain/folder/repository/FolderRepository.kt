package com.on.turip.domain.folder.repository

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.domain.folder.Folder

interface FolderRepository {
    suspend fun loadFavoriteFolders(): TuripCustomResult<List<Folder>>

    suspend fun createFavoriteFolder(name: String): TuripCustomResult<Unit>

    suspend fun updateFavoriteFolder(
        folderId: Long,
        updateName: String,
    ): TuripCustomResult<Unit>
}
