package com.on.turip.data.favorite.datasource

import com.on.turip.data.favorite.dto.FavoriteAddRequest
import com.on.turip.data.favorite.service.FavoriteService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class DefaultFavoriteRemoteDataSource(
    private val favoriteService: FavoriteService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : FavoriteRemoteDataSource {
    override suspend fun postFavorite(
        fid: String,
        favoriteAddRequest: FavoriteAddRequest,
    ): Result<Unit> =
        withContext(coroutineContext) {
            runCatching {
                favoriteService.postFavorite(fid, favoriteAddRequest)
            }
        }

    override suspend fun deleteFavorite(
        fid: String,
        contentId: Long,
    ): Result<Unit> =
        withContext(coroutineContext) {
            runCatching {
                favoriteService.deleteFavorite(fid, contentId)
            }
        }
}
