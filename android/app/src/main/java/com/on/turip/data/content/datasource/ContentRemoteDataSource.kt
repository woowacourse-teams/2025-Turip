package com.on.turip.data.content.datasource

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.content.dto.ContentDetailResponse
import com.on.turip.data.content.dto.ContentInformationCountResponse
import com.on.turip.data.content.dto.ContentsInformationResponse
import com.on.turip.data.content.dto.ContentsInformationResponse2
import com.on.turip.data.content.dto.UsersLikeContentsResponse

interface ContentRemoteDataSource {
    suspend fun getContentsSizeByRegion(regionCategoryName: String): TuripCustomResult<ContentInformationCountResponse>

    suspend fun getContentsSizeByKeyword(keyword: String): TuripCustomResult<ContentInformationCountResponse>

    suspend fun getContentsByRegion(
        regionCategoryName: String,
        size: Int,
        lastId: Long,
    ): TuripCustomResult<ContentsInformationResponse>

    suspend fun getContentsByRegion2(
        regionCategoryName: String,
        size: Int,
        lastId: Long,
    ): TuripCustomResult<ContentsInformationResponse2>

    suspend fun getContentsByKeyword(
        keyword: String,
        size: Int,
        lastId: Long,
    ): TuripCustomResult<ContentsInformationResponse>

    suspend fun getContentDetail(contentId: Long): TuripCustomResult<ContentDetailResponse>

    suspend fun getUsersLikeContents(size: Int): TuripCustomResult<UsersLikeContentsResponse>
}
