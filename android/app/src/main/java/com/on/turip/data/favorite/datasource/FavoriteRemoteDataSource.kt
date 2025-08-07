package com.on.turip.data.favorite.datasource

import com.on.turip.data.favorite.dto.FavoriteAddRequest
import com.on.turip.data.favorite.dto.FavoriteContentsResponse

interface FavoriteRemoteDataSource {
    suspend fun postFavorite(
        fid: String,
        favoriteAddRequest: FavoriteAddRequest,
    ): Result<Unit>

    suspend fun deleteFavorite(
        fid: String,
        contentId: Long,
    ): Result<Unit>

    suspend fun getFavoriteContents(
        fid: String,
        size: Int,
        lastId: Long,
    ): Result<FavoriteContentsResponse>
}
