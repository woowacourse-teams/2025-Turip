package com.on.turip.domain.contents.repository

import com.on.turip.domain.contents.PagedContentsResult

interface ContentRepository {
    suspend fun loadContentsSize(region: String): Int

    suspend fun loadContents(
        region: String,
        size: Int,
        lastId: Long,
    ): PagedContentsResult
}
