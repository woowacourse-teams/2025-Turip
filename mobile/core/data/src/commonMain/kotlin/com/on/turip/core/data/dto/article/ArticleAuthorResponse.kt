package com.on.turip.core.data.dto.article

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArticleAuthorResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("nickname")
    val name: String,
)
