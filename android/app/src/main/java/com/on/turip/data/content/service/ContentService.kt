package com.on.turip.data.content.service

import com.on.turip.data.content.dto.ContentInformationCountResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ContentService {
    @GET("contents/count")
    suspend fun getContentsCount(
        @Query("region") region: String,
    ): ContentInformationCountResponse
}
