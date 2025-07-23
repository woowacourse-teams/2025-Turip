package com.on.turip.data.contents.service

import com.on.turip.data.contents.dto.ContentsCountResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ContentsService {
    @GET("contents/count")
    suspend fun getContentsCount(
        @Query("region") region: String,
    ): ContentsCountResponse
}
