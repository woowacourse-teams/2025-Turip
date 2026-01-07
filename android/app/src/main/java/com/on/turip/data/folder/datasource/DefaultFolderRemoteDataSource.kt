package com.on.turip.data.folder.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.data.folder.dto.FavoriteFolderCreationResponse
import com.on.turip.data.folder.dto.FavoriteFolderPatchRequest
import com.on.turip.data.folder.dto.FavoriteFolderPostRequest
import com.on.turip.data.folder.dto.FavoriteFoldersResponse
import com.on.turip.data.folder.dto.FavoriteFoldersStatusByPlaceResponse
import com.on.turip.data.folder.service.FolderService
import com.on.turip.data.result.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DefaultFolderRemoteDataSource @Inject constructor(
    private val folderService: FolderService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : FolderRemoteDataSource {
    override suspend fun getFavoriteFolders(): TuripResult<FavoriteFoldersResponse> =
        withContext(coroutineContext) {
            safeApiCall {
                folderService.getFavoriteFolders()
            }
        }

    override suspend fun postFavoriteFolder(
        favoriteFolderPostRequest: FavoriteFolderPostRequest,
    ): TuripResult<FavoriteFolderCreationResponse> =
        withContext(coroutineContext) {
            safeApiCall {
                folderService.postFavoriteFolder(favoriteFolderPostRequest)
            }
        }

    override suspend fun patchFavoriteFolder(
        folderId: Long,
        favoriteFolderPatchRequest: FavoriteFolderPatchRequest,
    ): TuripResult<Unit> =
        withContext(coroutineContext) {
            safeApiCall {
                folderService.patchFavoriteFolder(folderId, favoriteFolderPatchRequest)
            }
        }

    override suspend fun deleteFavoriteFolder(folderId: Long): TuripResult<Unit> =
        withContext(coroutineContext) {
            safeApiCall {
                folderService.deleteFavoriteFolder(folderId)
            }
        }

    override suspend fun getFavoriteFoldersStatusByPlaceId(placeId: Long): TuripResult<FavoriteFoldersStatusByPlaceResponse> =
        withContext(coroutineContext) {
            safeApiCall {
                folderService.getFavoriteFoldersStatusByPlaceId(placeId)
            }
        }
}
