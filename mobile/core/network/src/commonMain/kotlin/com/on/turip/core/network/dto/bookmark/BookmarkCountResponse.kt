package com.on.turip.core.network.dto.bookmark

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookmarkCountResponse(
    @SerialName("count")
    val count: Int,
)
