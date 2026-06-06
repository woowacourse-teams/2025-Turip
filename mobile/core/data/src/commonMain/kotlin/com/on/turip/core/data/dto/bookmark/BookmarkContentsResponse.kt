package com.on.turip.core.data.dto.bookmark

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookmarkContentsResponse(
    @SerialName("bookmarks")
    val bookmarks: List<BookmarkContentResponse>,
    @SerialName("loadable")
    val loadable: Boolean,
)
