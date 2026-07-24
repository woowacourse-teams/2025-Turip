package com.on.turip.core.domain.repository

import com.on.turip.core.model.content.Content
import com.on.turip.core.model.content.PagedContentsResult
import com.on.turip.core.model.content.UsersLikeContent
import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.model.trip.Trip

interface ContentRepository {
    suspend fun loadContentsSizeByRegion(regionCategoryName: String): TuripResult<Int>

    suspend fun loadContentsSizeByKeyword(keyword: String): TuripResult<Int>

    suspend fun loadContentsByRegion(
        regionCategoryName: String,
        size: Int,
        lastId: Long,
    ): TuripResult<PagedContentsResult>

    suspend fun loadContentsByKeyword(
        keyword: String,
        size: Int,
        lastId: Long,
    ): TuripResult<PagedContentsResult>

    suspend fun loadContent(contentId: Long): TuripResult<Content>

    suspend fun loadPopularFavoriteContents(size: Int = 5): TuripResult<List<UsersLikeContent>>

    suspend fun loadTripInfo(contentId: Long): TuripResult<Trip>
}
