package com.on.turip.domain.bookmark

import com.on.turip.domain.content.Content
import com.on.turip.domain.trip.TripDuration

data class BookmarkContent(
    val bookmarkId: Long,
    val content: Content,
    val tripDuration: TripDuration,
    val tripPlaceCount: Int,
    val createdAt: String,
)
