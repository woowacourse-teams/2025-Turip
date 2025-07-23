package com.on.turip.data.content.repository

import com.on.turip.data.content.dataSource.ContentRemoteDataSource
import com.on.turip.domain.contents.PagedContentsResult
import com.on.turip.domain.contents.repository.ContentRepository

class DefaultContentRepository(
    private val contentRemoteDataSource: ContentRemoteDataSource,
) : ContentRepository {
    override suspend fun loadContentsSize(region: String): Result<Int> =
        contentRemoteDataSource
            .getContentsSize(region)
            .mapCatching { it.count }

    override suspend fun loadContents(
        region: String,
        size: Int,
        lastId: Long,
    ): PagedContentsResult {
        return PagedContentsResult(videos = emptyList(), false)
        // TODO("추후 API 연결")
    }
}
