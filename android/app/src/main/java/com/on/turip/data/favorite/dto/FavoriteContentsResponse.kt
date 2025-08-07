package com.on.turip.data.favorite.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteContentsResponse(
    @SerialName("contents")
    val contents: List<FavoriteContentResponse>,
    @SerialName("loadable")
    val loadable: Boolean,
)
