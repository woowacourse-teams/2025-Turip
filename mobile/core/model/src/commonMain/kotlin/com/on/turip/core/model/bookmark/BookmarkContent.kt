package com.on.turip.core.model.bookmark

import com.on.turip.core.model.content.Content
import com.on.turip.core.model.trip.TripDuration

data class BookmarkContent(
    val bookmarkId: Long,
    val content: Content,
    val tripDuration: TripDuration,
    val tripPlaceCount: Int,
    val createdAt: String,
) {
    companion object {
        fun of(
            bookmark: Bookmark,
            tripDuration: TripDuration,
            tripPlaceCount: Int,
        ): BookmarkContent =
            BookmarkContent(
                bookmarkId = bookmark.id,
                content = bookmark.content,
                tripDuration = tripDuration,
                tripPlaceCount = tripPlaceCount,
                createdAt = bookmark.createdAt,
            )
    }
}
