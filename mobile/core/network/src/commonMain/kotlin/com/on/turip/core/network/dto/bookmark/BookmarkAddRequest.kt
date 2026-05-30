package com.on.turip.core.network.dto.bookmark

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookmarkAddRequest(
    @SerialName("contentId")
    val contentId: Long,
)
