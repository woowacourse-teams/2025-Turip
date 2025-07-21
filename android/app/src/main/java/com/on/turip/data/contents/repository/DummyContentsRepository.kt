package com.on.turip.data.contents.repository

import com.on.turip.domain.contents.PagedContentsResult
import com.on.turip.domain.contents.Video
import com.on.turip.domain.contents.repository.ContentsRepository

class DummyContentsRepository(
    private val dummyData: List<Video> = Video.dummyVideos,
) : ContentsRepository {
    override suspend fun loadContentsSize(region: String): Int = dummyData.size

    override suspend fun loadContents(
        region: String,
        size: Int,
        lastId: Long,
    ): PagedContentsResult =
        PagedContentsResult(
            videos = dummyData,
            loadable = false,
        )
}
