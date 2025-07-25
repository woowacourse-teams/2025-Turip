package com.on.turip.data.content.dataSource

import com.on.turip.data.content.dto.ContentDetailResponse
import com.on.turip.data.content.dto.ContentInformationCountResponse
import com.on.turip.data.content.dto.ContentsInformationResponse

interface ContentRemoteDataSource {
    suspend fun getContentsSize(region: String): Result<ContentInformationCountResponse>

    suspend fun getContents(
        region: String,
        size: Int,
        lastId: Long,
    ): Result<ContentsInformationResponse>

    suspend fun getContentDetail(contentId: Long): Result<ContentDetailResponse>
}
