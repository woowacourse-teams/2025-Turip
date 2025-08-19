package com.on.turip.data.creator.service

import com.on.turip.data.creator.dto.CreatorResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CreatorService {
    @GET("creators/{creatorId}")
    suspend fun getCreator(
        @Path("creatorId") creatorId: Long,
    ): Response<CreatorResponse>
}
