package com.on.turip.core.data.dto.article

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArticleResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("title")
    val title: String,
    @SerialName("subtitle")
    val subtitle: String,
    @SerialName("thumbnailUrl")
    val thumbnailUrl: String,
    @SerialName("tags")
    val tags: List<String>,
    @SerialName("author")
    val author: ArticleAuthorResponse?,
    @SerialName("createdAt")
    val createdAt: String,
)
