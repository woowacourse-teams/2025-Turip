package com.on.turip.domain.favorite.repository

interface FavoriteRepository {
    suspend fun createFavorite(contentId: Long): Result<Unit>

    suspend fun deleteFavorite(contentId: Long): Result<Unit>
}
