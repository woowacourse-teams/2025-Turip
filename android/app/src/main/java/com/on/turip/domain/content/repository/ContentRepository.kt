package com.on.turip.domain.content.repository

import com.on.turip.domain.content.PagedContentsResult
import com.on.turip.domain.content.PopularFavoriteContent
import com.on.turip.domain.content.video.VideoData

interface ContentRepository {
    suspend fun loadContentsSize(region: String): Result<Int>

    suspend fun loadContents(
        region: String,
        size: Int,
        lastId: Long,
    ): Result<PagedContentsResult>

    suspend fun loadContent(contentId: Long): Result<VideoData>

    suspend fun loadPopularFavoriteContents(): Result<List<PopularFavoriteContent>>
}
