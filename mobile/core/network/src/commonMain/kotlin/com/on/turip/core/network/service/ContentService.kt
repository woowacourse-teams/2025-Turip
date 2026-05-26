package com.on.turip.core.network.service

import com.on.turip.core.network.ApiPath
import com.on.turip.core.network.dto.content.ContentResponse
import com.on.turip.core.network.dto.content.UsersLikeContentResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface ContentService {
    @GET(ApiPath.V1 + "contents/{contentId}")
    suspend fun getContent(
        @Path("contentId") contentId: Long,
    ): ContentResponse

    @GET(ApiPath.V1 + "contents/popular")
    suspend fun getPopularContents(
        @Query("size") size: Int,
    ): List<UsersLikeContentResponse>

    @GET(ApiPath.V1 + "contents/keyword")
    suspend fun getContentsByKeyword(
        @Query("keyword") keyword: String,
        @Query("size") size: Int,
        @Query("lastId") lastId: Long,
    ): List<ContentResponse>

    @GET(ApiPath.V1 + "contents/keyword/count")
    suspend fun getContentsSizeByKeyword(
        @Query("keyword") keyword: String,
    ): Int

    @GET(ApiPath.V1 + "contents")
    suspend fun getContentsByRegion(
        @Query("regionCategory") regionCategory: String,
        @Query("size") size: Int,
        @Query("lastId") lastId: Long,
    ): List<ContentResponse>

    @GET(ApiPath.V1 + "contents/count")
    suspend fun getContentsSizeByRegion(
        @Query("regionCategory") regionCategory: String,
    ): Int
}
