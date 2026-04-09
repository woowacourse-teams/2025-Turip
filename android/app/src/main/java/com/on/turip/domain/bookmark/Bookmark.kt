package com.on.turip.domain.bookmark

import com.on.turip.domain.content.Content

data class Bookmark(
    val id: Long,
    val content: Content,
    val createdAt: String,
)
