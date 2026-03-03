package com.on.turip.data.bookmark.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookmarkCountResponse(
    @SerialName("count")
    val count: Int,
)
