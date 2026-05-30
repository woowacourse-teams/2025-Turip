package com.on.turip.core.model.bookmark

import com.on.turip.core.model.content.Content

data class Bookmark(
    val id: Long,
    val content: Content,
    val createdAt: String,
)
