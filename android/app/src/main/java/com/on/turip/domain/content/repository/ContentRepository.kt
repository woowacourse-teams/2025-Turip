package com.on.turip.domain.content.repository

import com.on.turip.domain.content.PagedContentsResult
import com.on.turip.domain.content.UsersLikeContent
import com.on.turip.domain.content.video.VideoData

interface ContentRepository {
    suspend fun loadContentsSizeByRegion(region: String): Result<Int>

    suspend fun loadContentsSizeByKeyword(keyword: String): Result<Int>

    suspend fun loadContentsByRegion(
        region: String,
        size: Int,
        lastId: Long,
    ): Result<PagedContentsResult>

    suspend fun loadContentsByKeyword(
        keyword: String,
        size: Int,
        lastId: Long,
    ): Result<PagedContentsResult>

    suspend fun loadContent(contentId: Long): Result<VideoData>

    suspend fun loadPopularFavoriteContents(size: Int = 5): Result<List<UsersLikeContent>>
}
