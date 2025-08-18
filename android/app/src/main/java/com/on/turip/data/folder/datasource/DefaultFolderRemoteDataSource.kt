package com.on.turip.data.folder.datasource

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.common.safeApiCall
import com.on.turip.data.folder.dto.FavoriteFoldersResponse
import com.on.turip.data.folder.service.FolderService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class DefaultFolderRemoteDataSource(
    private val folderService: FolderService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : FolderRemoteDataSource {
    override suspend fun getFavoriteFolders(): TuripCustomResult<FavoriteFoldersResponse> =
        withContext(coroutineContext) {
            safeApiCall {
                folderService.getFavoriteFolders()
            }
        }
}
