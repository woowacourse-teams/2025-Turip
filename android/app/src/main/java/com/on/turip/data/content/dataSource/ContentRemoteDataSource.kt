package com.on.turip.data.content.dataSource

import com.on.turip.data.content.dto.ContentCountResponse

interface ContentRemoteDataSource {
    suspend fun getContentsSize(region: String): Result<ContentCountResponse>
}
