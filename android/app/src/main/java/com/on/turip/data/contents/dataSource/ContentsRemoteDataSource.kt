package com.on.turip.data.contents.dataSource

import com.on.turip.data.contents.dto.ContentsCountResponse

interface ContentsRemoteDataSource {
    suspend fun getContentsSize(region: String): ContentsCountResponse
}
