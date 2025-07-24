package com.on.turip.data.creator.service

import com.on.turip.data.creator.dto.CreatorResponse
import retrofit2.http.GET

interface CreatorService {
    @GET("creators/{creatorId}")
    suspend fun getCreator(creatorId: Long): CreatorResponse
}
