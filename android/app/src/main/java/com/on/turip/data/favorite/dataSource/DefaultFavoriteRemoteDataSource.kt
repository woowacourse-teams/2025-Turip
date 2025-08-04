package com.on.turip.data.favorite.dataSource

import com.on.turip.data.favorite.dto.FavoriteAddRequest
import com.on.turip.data.favorite.service.FavoriteService
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultFavoriteRemoteDataSource(
    private val favoriteService: FavoriteService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : FavoriteRemoteDataSource {
    override suspend fun postFavorite(
        fid: String,
        favoriteAddRequest: FavoriteAddRequest,
    ) {
        withContext(coroutineContext) {
            favoriteService.postFavorite(fid, favoriteAddRequest)
        }
    }
}
