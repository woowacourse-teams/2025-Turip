package com.on.turip.domain.content.repository

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.domain.content.Content
import com.on.turip.domain.content.PagedContentsResult
import com.on.turip.domain.content.UsersLikeContent

interface ContentRepository {
    suspend fun loadContentsSizeByRegion(regionCategoryName: String): TuripCustomResult<Int>

    suspend fun loadContentsSizeByKeyword(keyword: String): TuripCustomResult<Int>

    suspend fun loadContentsByRegion(
        regionCategoryName: String,
        size: Int,
        lastId: Long,
    ): TuripCustomResult<PagedContentsResult>

    suspend fun loadContentsByKeyword(
        keyword: String,
        size: Int,
        lastId: Long,
    ): TuripCustomResult<PagedContentsResult>

    suspend fun loadContent(contentId: Long): TuripCustomResult<Content>

    suspend fun loadPopularFavoriteContents(size: Int = 5): TuripCustomResult<List<UsersLikeContent>>
}
