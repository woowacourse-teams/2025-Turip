package com.on.turip.domain.contents.repository

import com.on.turip.domain.contents.PagedContentsResult

interface ContentRepository {
    suspend fun loadContentsSize(region: String): Result<Int>

    suspend fun loadContents(
        region: String,
        size: Int,
        lastId: Long,
    ): Result<PagedContentsResult>
}
