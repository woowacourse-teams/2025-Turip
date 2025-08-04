package com.on.turip.data.favorite.dataSource

import com.on.turip.data.favorite.dto.FavoriteAddRequest

interface FavoriteRemoteDataSource {
    suspend fun postFavorite(
        fid: String,
        favoriteAddRequest: FavoriteAddRequest,
    )

    suspend fun deleteFavorite(
        fid: String,
        contentId: Long,
    )
}
