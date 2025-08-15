package com.on.turip.domain.favorite.repository

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.domain.favorite.PagedFavoriteContents

interface FavoriteRepository {
    suspend fun createFavorite(contentId: Long): TuripCustomResult<Unit>

    suspend fun deleteFavorite(contentId: Long): TuripCustomResult<Unit>

    suspend fun loadFavoriteContents(
        size: Int,
        lastId: Long,
    ): TuripCustomResult<PagedFavoriteContents>
}
