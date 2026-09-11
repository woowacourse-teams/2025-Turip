package com.on.turip.core.data.dto.article

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArticlesResponse(
    @SerialName("articles")
    val articles: List<ArticleResponse>,
    @SerialName("loadable")
    val loadable: Boolean,
)
