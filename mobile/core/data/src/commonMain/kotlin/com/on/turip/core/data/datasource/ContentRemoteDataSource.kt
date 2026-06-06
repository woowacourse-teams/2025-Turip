package com.on.turip.core.data.datasource

import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.data.dto.content.ContentDetailResponse
import com.on.turip.core.data.dto.content.ContentInformationCountResponse
import com.on.turip.core.data.dto.content.ContentPlacesResponse
import com.on.turip.core.data.dto.content.ContentsInformationResponse
import com.on.turip.core.data.dto.content.UsersLikeContentsResponse

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

    suspend fun getTrip(contentId: Long): TuripResult<ContentPlacesResponse>
}
