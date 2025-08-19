package com.on.turip.domain.favorite.repository

import com.on.turip.domain.favorite.PagedFavoriteContents

interface FavoriteRepository {
    suspend fun createFavorite(contentId: Long): Result<Unit>

    suspend fun deleteFavorite(contentId: Long): Result<Unit>

    suspend fun loadFavoriteContents(
        size: Int,
        lastId: Long,
    ): Result<PagedFavoriteContents>
}
