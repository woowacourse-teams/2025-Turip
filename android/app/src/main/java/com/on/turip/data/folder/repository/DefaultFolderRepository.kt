package com.on.turip.data.folder.repository

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.common.mapCatching
import com.on.turip.data.folder.datasource.FolderRemoteDataSource
import com.on.turip.data.folder.toAddRequestDto
import com.on.turip.data.folder.toDomain
import com.on.turip.data.folder.toPatchRequestDto
import com.on.turip.domain.folder.Folder
import com.on.turip.domain.folder.repository.FolderRepository

class DefaultFolderRepository(
    private val folderRemoteDataSource: FolderRemoteDataSource,
) : FolderRepository {
    override suspend fun loadFavoriteFolders(): TuripCustomResult<List<Folder>> =
        folderRemoteDataSource.getFavoriteFolders().mapCatching { it.toDomain() }

    override suspend fun createFavoriteFolder(name: String): TuripCustomResult<Unit> =
        folderRemoteDataSource.postFavoriteFolder(name.toAddRequestDto())

    override suspend fun updateFavoriteFolder(
        folderId: Long,
        updateName: String,
    ): TuripCustomResult<Unit> = folderRemoteDataSource.patchFavoriteFolder(folderId, updateName.toPatchRequestDto())
}
