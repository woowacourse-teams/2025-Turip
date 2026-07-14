package com.on.turip.core.data.dto.bookmark

import com.on.turip.core.data.dto.content.ContentResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookmarkCreationResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("accountId")
    val accountId: Long,
    @SerialName("content")
    val content: ContentResponse,
)
