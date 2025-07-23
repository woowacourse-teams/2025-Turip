package com.on.turip.data.contents.dataSource

import com.on.turip.data.contents.dto.ContentsCountResponse
import com.on.turip.data.contents.service.ContentsService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultContentsRemoteDataSource(
    private val contentsService: ContentsService,
) : ContentsRemoteDataSource {
    override suspend fun getContentsSize(region: String): ContentsCountResponse =
        withContext(Dispatchers.IO) {
            contentsService.getContentsCount(region)
        }
}
