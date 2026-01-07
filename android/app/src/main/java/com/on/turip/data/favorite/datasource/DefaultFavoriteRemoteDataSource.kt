package com.on.turip.data.favorite.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.data.favorite.dto.FavoriteAddRequest
import com.on.turip.data.favorite.dto.FavoriteContentsResponse
import com.on.turip.data.favorite.service.FavoriteService
import com.on.turip.data.result.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DefaultFavoriteRemoteDataSource @Inject constructor(
    private val favoriteService: FavoriteService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : FavoriteRemoteDataSource {
    override suspend fun postFavorite(favoriteAddRequest: FavoriteAddRequest): TuripResult<Unit> =
        withContext(coroutineContext) {
            safeApiCall {
                favoriteService.postFavorite(favoriteAddRequest)
            }
        }

    override suspend fun deleteFavorite(contentId: Long): TuripResult<Unit> =
        withContext(coroutineContext) {
            safeApiCall {
                favoriteService.deleteFavorite(contentId)
            }
        }

    override suspend fun getFavoriteContents(
        size: Int,
        lastId: Long,
    ): TuripResult<FavoriteContentsResponse> =
        withContext(coroutineContext) {
            safeApiCall { favoriteService.getFavoriteContents(size, lastId) }
        }
}
