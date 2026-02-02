package com.on.turip.data.bookmarks.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookmarkContentsResponse(
    @SerialName("contents")
    val contents: List<BookmarkContentResponse>,
    @SerialName("loadable")
    val loadable: Boolean,
)
