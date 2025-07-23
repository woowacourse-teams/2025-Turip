package com.on.turip.data.content.dataSource

import com.on.turip.data.content.dto.ContentInformationCountResponse

interface ContentRemoteDataSource {
    suspend fun getContentsSize(region: String): Result<ContentInformationCountResponse>

    suspend fun getContents(region: String): Result<>
}
