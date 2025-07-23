package com.on.turip.domain.videoinfo.contents.repository

import com.on.turip.domain.videoinfo.contents.PagedContentsResult

interface ContentRepository {
    suspend fun loadContentsSize(region: String): Int

    suspend fun loadContents(
        region: String,
        size: Int,
        lastId: Long,
    ): PagedContentsResult
}
