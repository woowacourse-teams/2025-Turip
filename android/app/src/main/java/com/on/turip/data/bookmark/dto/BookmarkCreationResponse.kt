package com.on.turip.data.bookmark.dto

import com.on.turip.data.content.dto.ContentResponse
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
