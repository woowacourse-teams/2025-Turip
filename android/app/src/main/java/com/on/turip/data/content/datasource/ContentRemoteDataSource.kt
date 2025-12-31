package com.on.turip.data.content.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.data.content.dto.ContentDetailResponse
import com.on.turip.data.content.dto.ContentInformationCountResponse
import com.on.turip.data.content.dto.ContentsInformationResponse
import com.on.turip.data.content.dto.UsersLikeContentsResponse

interface ContentRemoteDataSource {
    suspend fun getContentsSizeByRegion(regionCategoryName: String): TuripResult<ContentInformationCountResponse>

    suspend fun getContentsSizeByKeyword(keyword: String): TuripResult<ContentInformationCountResponse>

    suspend fun getContentsByRegion(
        regionCategoryName: String,
        size: Int,
        lastId: Long,
    ): TuripResult<ContentsInformationResponse>

    suspend fun getContentsByKeyword(
        keyword: String,
        size: Int,
        lastId: Long,
    ): TuripResult<ContentsInformationResponse>

    suspend fun getContentDetail(contentId: Long): TuripResult<ContentDetailResponse>

    suspend fun getUsersLikeContents(size: Int): TuripResult<UsersLikeContentsResponse>
}
