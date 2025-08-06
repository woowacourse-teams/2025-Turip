package com.on.turip.data.favorite.datasource

import com.on.turip.data.favorite.dto.FavoriteAddRequest

interface FavoriteRemoteDataSource {
    suspend fun postFavorite(
        fid: String,
        favoriteAddRequest: FavoriteAddRequest,
    ): Result<Unit>

    suspend fun deleteFavorite(
        fid: String,
        contentId: Long,
    ): Result<Unit>
}
