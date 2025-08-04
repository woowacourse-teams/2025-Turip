package com.on.turip.domain.favorite.repository

interface FavoriteRepository {
    suspend fun updateFavorite(
        isFavorite: Boolean,
        fid: String,
        contentId: Long,
    )
}
