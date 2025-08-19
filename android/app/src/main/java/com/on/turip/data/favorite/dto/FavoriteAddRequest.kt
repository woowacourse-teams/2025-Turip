package com.on.turip.data.favorite.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteAddRequest(
    @SerialName("contentId")
    val contentId: Long,
)
