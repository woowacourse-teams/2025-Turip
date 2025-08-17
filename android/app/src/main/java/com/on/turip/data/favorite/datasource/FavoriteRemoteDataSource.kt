package com.on.turip.data.favorite.datasource

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.favorite.dto.FavoriteAddRequest
import com.on.turip.data.favorite.dto.FavoriteContentsResponse

interface FavoriteRemoteDataSource {
    suspend fun postFavorite(favoriteAddRequest: FavoriteAddRequest): TuripCustomResult<Unit>

    suspend fun deleteFavorite(contentId: Long): TuripCustomResult<Unit>

    suspend fun getFavoriteContents(
        size: Int,
        lastId: Long,
    ): TuripCustomResult<FavoriteContentsResponse>
}
