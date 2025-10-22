package com.on.turip.data.folder.repository

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.common.mapCatching
import com.on.turip.data.folder.datasource.FolderRemoteDataSource
import com.on.turip.data.folder.toDomain
import com.on.turip.data.folder.toPatchRequestDto
import com.on.turip.data.folder.toPostRequestDto
import com.on.turip.domain.folder.FavoriteFolder
import com.on.turip.domain.folder.Folder
import com.on.turip.domain.folder.repository.FolderRepository

class DefaultFolderRepository(
    private val folderRemoteDataSource: FolderRemoteDataSource,
) : FolderRepository {
    override suspend fun loadFavoriteFolders(): TuripCustomResult<List<Folder>> =
        folderRemoteDataSource.getFavoriteFolders().mapCatching { it.toDomain() }

    override suspend fun createFavoriteFolder(name: String): TuripCustomResult<Folder> =
        folderRemoteDataSource
            .postFavoriteFolder(name.toPostRequestDto())
            .mapCatching { it.toDomain() }

    override suspend fun updateFavoriteFolder(
        folderId: Long,
        updateName: String,
    ): TuripCustomResult<Unit> = folderRemoteDataSource.patchFavoriteFolder(folderId, updateName.toPatchRequestDto())

    override suspend fun deleteFavoriteFolder(folderId: Long): TuripCustomResult<Unit> =
        folderRemoteDataSource.deleteFavoriteFolder(folderId)

    override suspend fun loadFavoriteFoldersStatusByPlaceId(placeId: Long): TuripCustomResult<List<FavoriteFolder>> =
        folderRemoteDataSource
            .getFavoriteFoldersStatusByPlaceId(placeId)
            .mapCatching { it.toDomain() }
}
