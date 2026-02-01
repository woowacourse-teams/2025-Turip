package com.on.turip.data.folder.repository

import com.on.turip.core.result.TuripResult
import com.on.turip.core.result.mapCatching
import com.on.turip.data.folder.datasource.FolderRemoteDataSource
import com.on.turip.data.folder.toDomain
import com.on.turip.data.folder.toPatchRequestDto
import com.on.turip.data.folder.toPostRequestDto
import com.on.turip.domain.folder.Folder
import com.on.turip.domain.folder.Turip
import com.on.turip.domain.folder.repository.turipRepository
import javax.inject.Inject

class DefaultTuripRepository @Inject constructor(
    private val folderRemoteDataSource: FolderRemoteDataSource,
) : turipRepository {
    override suspend fun loadFavoriteFolders(): TuripResult<List<Folder>> =
        folderRemoteDataSource.getFavoriteFolders().mapCatching { it.toDomain() }

    override suspend fun createFavoriteFolder(name: String): TuripResult<Folder> =
        folderRemoteDataSource
            .postFavoriteFolder(name.toPostRequestDto())
            .mapCatching { it.toDomain() }

    override suspend fun updateFavoriteFolder(
        folderId: Long,
        updateName: String,
    ): TuripResult<Unit> = folderRemoteDataSource.patchFavoriteFolder(folderId, updateName.toPatchRequestDto())

    override suspend fun deleteFavoriteFolder(folderId: Long): TuripResult<Unit> = folderRemoteDataSource.deleteFavoriteFolder(folderId)

    override suspend fun loadTuripsByPlaceId(placeId: Long): TuripResult<List<Turip>> =
        folderRemoteDataSource
            .getFavoriteFoldersStatusByPlaceId(placeId)
            .mapCatching { it.toDomain() }
}
