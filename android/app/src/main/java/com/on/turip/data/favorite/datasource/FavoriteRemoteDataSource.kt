package com.on.turip.data.favorite.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.data.favorite.dto.FavoriteAddRequest
import com.on.turip.data.favorite.dto.FavoriteContentsResponse

interface FavoriteRemoteDataSource {
    suspend fun postFavorite(favoriteAddRequest: FavoriteAddRequest): TuripResult<Unit>

    suspend fun deleteFavorite(contentId: Long): TuripResult<Unit>

    suspend fun getFavoriteContents(
        size: Int,
        lastId: Long,
    ): TuripResult<FavoriteContentsResponse>
}
