package com.on.turip.data.favorite.datasource

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.common.safeApiCall
import com.on.turip.data.favorite.dto.FavoriteAddRequest
import com.on.turip.data.favorite.dto.FavoriteContentsResponse
import com.on.turip.data.favorite.service.FavoriteService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class DefaultFavoriteRemoteDataSource(
    private val favoriteService: FavoriteService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : FavoriteRemoteDataSource {
    override suspend fun postFavorite(favoriteAddRequest: FavoriteAddRequest): TuripCustomResult<Unit> =
        withContext(coroutineContext) {
            safeApiCall {
                favoriteService.postFavorite(favoriteAddRequest)
            }
        }

    override suspend fun deleteFavorite(contentId: Long): TuripCustomResult<Unit> =
        withContext(coroutineContext) {
            safeApiCall {
                favoriteService.deleteFavorite(contentId)
            }
        }

    override suspend fun getFavoriteContents(
        size: Int,
        lastId: Long,
    ): TuripCustomResult<FavoriteContentsResponse> =
        withContext(coroutineContext) {
            safeApiCall { favoriteService.getFavoriteContents(size, lastId) }
        }
}
