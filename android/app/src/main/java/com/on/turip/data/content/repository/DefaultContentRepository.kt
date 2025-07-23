package com.on.turip.data.content.repository

import com.on.turip.data.content.dataSource.ContentRemoteDataSource
import com.on.turip.domain.contents.PagedContentsResult
import com.on.turip.domain.contents.repository.ContentRepository

class DefaultContentRepository(
    private val contentRemoteDataSource: ContentRemoteDataSource,
) : ContentRepository {
    override suspend fun loadContentsSize(region: String): Result<Int> {
        contentRemoteDataSource.getContentsSize(region).count
    }
        runCatching {
            contentRemoteDataSource.getContentsSize(region).count
        }.onSuccess { contentsSize ->
            contentsSize
        }.onFailure {
            it.message
            // TODO: 어떤 에러인지 반환
        }

    override suspend fun loadContents(
        region: String,
        size: Int,
        lastId: Long,
    ): PagedContentsResult {
        return PagedContentsResult(videos = emptyList(), false)
        // TODO("추후 API 연결")
    }
}
