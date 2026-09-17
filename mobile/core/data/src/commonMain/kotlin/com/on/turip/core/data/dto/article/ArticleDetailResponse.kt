package com.on.turip.core.data.dto.article

import com.on.turip.core.data.dto.content.PlaceResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArticleDetailResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("title")
    val title: String,
    @SerialName("subtitle")
    val subtitle: String,
    @SerialName("content")
    val content: String,
    @SerialName("thumbnailUrl")
    val thumbnailUrl: String,
    @SerialName("tags")
    val tags: List<String>,
    @SerialName("author")
    val author: ArticleAuthorResponse?,
    @SerialName("places")
    val places: List<PlaceResponse>,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("updatedAt")
    val updatedAt: String,
)
