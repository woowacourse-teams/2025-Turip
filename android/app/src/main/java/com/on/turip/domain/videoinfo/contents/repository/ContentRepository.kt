package com.on.turip.domain.videoinfo.contents.repository

import com.on.turip.domain.videoinfo.contents.PagedContentsResult
import com.on.turip.domain.videoinfo.contents.VideoData

interface ContentRepository {
    suspend fun loadContentsSize(region: String): Result<Int>

    suspend fun loadContents(
        region: String,
        size: Int,
        lastId: Long,
    ): Result<PagedContentsResult>

    suspend fun loadContent(contentId: Long): Result<VideoData>
}
