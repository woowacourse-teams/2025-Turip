package com.on.turip.core.network.service

import com.on.turip.core.data.dto.content.ContentDetailResponse
import com.on.turip.core.data.dto.content.ContentInformationCountResponse
import com.on.turip.core.data.dto.content.ContentPlacesResponse
import com.on.turip.core.data.dto.content.ContentResponse
import com.on.turip.core.data.dto.content.ContentsInformationResponse
import com.on.turip.core.data.dto.content.UsersLikeContentResponse
import com.on.turip.core.data.dto.content.UsersLikeContentsResponse
import com.on.turip.core.network.ApiPath
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface ContentService {
    @GET(ApiPath.V1 + "contents/count")
    suspend fun getContentsCountByRegion(
        @Query("regionCategory") regionCategoryName: String,
    ): ContentInformationCountResponse

    @GET(ApiPath.V1 + "contents/keyword/count")
    suspend fun getContentsCountByKeyword(
        @Query("keyword") keyword: String,
    ): ContentInformationCountResponse

    @GET(ApiPath.V1 + "contents")
    suspend fun getContentsByRegion(
        @Query("regionCategory") regionCategoryName: String,
        @Query("size") size: Int,
        @Query("lastId") lastId: Long,
    ): ContentsInformationResponse

    @GET(ApiPath.V1 + "contents/keyword")
    suspend fun getContentsByKeyword(
        @Query("keyword") keyword: String,
        @Query("size") size: Int,
        @Query("lastId") lastId: Long,
    ): ContentsInformationResponse

    @GET(ApiPath.V1 + "contents/{contentId}")
    suspend fun getContentDetail(
        @Path("contentId") contentId: Long,
    ): ContentDetailResponse

    @GET(ApiPath.V1 + "contents/popular")
    suspend fun getUsersLikeContents(
        @Query("size") size: Int,
    ): UsersLikeContentsResponse

    @GET(ApiPath.V1 + "contents/{contentId}/places")
    suspend fun getTrip(
        @Path("contentId") contentId: Long,
    ): ContentPlacesResponse
}
