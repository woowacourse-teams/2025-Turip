package com.on.turip.data.folder.datasource

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.common.safeApiCall
import com.on.turip.data.folder.dto.FavoriteFolderAddRequest
import com.on.turip.data.folder.dto.FavoriteFolderPatchRequest
import com.on.turip.data.folder.dto.FavoriteFoldersResponse
import com.on.turip.data.folder.dto.FavoriteFoldersStatusByPlaceResponse
import com.on.turip.data.folder.service.FolderService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DefaultFolderRemoteDataSource @Inject constructor(
    private val folderService: FolderService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : FolderRemoteDataSource {
    override suspend fun getFavoriteFolders(): TuripCustomResult<FavoriteFoldersResponse> =
        withContext(coroutineContext) {
            safeApiCall {
                folderService.getFavoriteFolders()
            }
        }

    override suspend fun postFavoriteFolder(favoriteFolderAddRequest: FavoriteFolderAddRequest): TuripCustomResult<Unit> =
        withContext(coroutineContext) {
            safeApiCall {
                folderService.postFavoriteFolder(favoriteFolderAddRequest)
            }
        }

    override suspend fun patchFavoriteFolder(
        folderId: Long,
        favoriteFolderPatchRequest: FavoriteFolderPatchRequest,
    ): TuripCustomResult<Unit> =
        withContext(coroutineContext) {
            safeApiCall {
                folderService.patchFavoriteFolder(folderId, favoriteFolderPatchRequest)
            }
        }

    override suspend fun deleteFavoriteFolder(folderId: Long): TuripCustomResult<Unit> =
        withContext(coroutineContext) {
            safeApiCall {
                folderService.deleteFavoriteFolder(folderId)
            }
        }

    override suspend fun getFavoriteFoldersStatusByPlaceId(placeId: Long): TuripCustomResult<FavoriteFoldersStatusByPlaceResponse> =
        withContext(coroutineContext) {
            safeApiCall {
                folderService.getFavoriteFoldersStatusByPlaceId(placeId)
            }
        }
}
