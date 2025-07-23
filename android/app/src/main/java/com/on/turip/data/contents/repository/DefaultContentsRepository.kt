package com.on.turip.data.contents.repository

import android.util.Log
import com.on.turip.data.contents.dataSource.ContentsRemoteDataSource
import com.on.turip.domain.contents.PagedContentsResult
import com.on.turip.domain.contents.repository.ContentsRepository

class DefaultContentsRepository(
    private val contentsRemoteDataSource: ContentsRemoteDataSource,
) : ContentsRepository {
    override suspend fun loadContentsSize(region: String): Result<Int> =
        runCatching {
            contentsRemoteDataSource.getContentsSize(region).count
        }.onSuccess { contentsSize ->
            contentsSize
        }.onFailure {
            Log.d("moongchi", "loadContentsSize:${it.message}")
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
