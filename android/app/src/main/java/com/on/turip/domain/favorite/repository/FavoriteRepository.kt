package com.on.turip.domain.favorite.repository

import com.on.turip.core.result.TuripResult
import com.on.turip.domain.favorite.PagedFavoriteContents

interface FavoriteRepository {
    suspend fun createFavorite(contentId: Long): TuripResult<Unit>

    suspend fun deleteFavorite(contentId: Long): TuripResult<Unit>

    suspend fun loadFavoriteContents(
        size: Int,
        lastId: Long,
    ): TuripResult<PagedFavoriteContents>
}
