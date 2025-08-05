package com.on.turip.domain.content.repository

import com.on.turip.domain.content.PagedContentsResult
import com.on.turip.domain.content.video.VideoData

interface ContentRepository {
    suspend fun loadContentsSize(region: String): Result<Int>

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
}
