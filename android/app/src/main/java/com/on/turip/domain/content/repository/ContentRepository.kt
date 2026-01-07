package com.on.turip.domain.content.repository

import com.on.turip.core.result.TuripResult
import com.on.turip.domain.content.Content
import com.on.turip.domain.content.PagedContentsResult
import com.on.turip.domain.content.UsersLikeContent

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
}
