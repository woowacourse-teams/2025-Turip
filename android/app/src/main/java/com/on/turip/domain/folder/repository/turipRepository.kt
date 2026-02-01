package com.on.turip.domain.folder.repository

import com.on.turip.core.result.TuripResult
import com.on.turip.domain.folder.Folder
import com.on.turip.domain.folder.Turip

interface turipRepository {
    suspend fun loadFavoriteFolders(): TuripResult<List<Folder>>

    suspend fun createFavoriteFolder(name: String): TuripResult<Folder>

    suspend fun updateFavoriteFolder(
        folderId: Long,
        updateName: String,
    ): TuripResult<Unit>

    suspend fun deleteFavoriteFolder(folderId: Long): TuripResult<Unit>

    suspend fun loadTuripsByPlaceId(placeId: Long): TuripResult<List<Turip>>
}
