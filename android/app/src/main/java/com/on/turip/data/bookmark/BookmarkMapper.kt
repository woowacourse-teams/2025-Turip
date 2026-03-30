package com.on.turip.data.bookmark

import com.on.turip.data.bookmark.dto.BookmarkAddRequest
import com.on.turip.data.bookmark.dto.BookmarkContentResponse
import com.on.turip.data.bookmark.dto.BookmarkContentsResponse
import com.on.turip.data.bookmark.dto.BookmarkCreationResponse
import com.on.turip.data.content.toDomain
import com.on.turip.domain.bookmark.BookmarkContent
import com.on.turip.domain.common.paging.Page
import com.on.turip.domain.trip.Trip

fun Long.toRequestDto(): BookmarkAddRequest = BookmarkAddRequest(contentId = this)

fun BookmarkContentsResponse.toDomain(): Page<BookmarkContent> =
    Page(
        items = bookmarks.map { it.toDomain() },
        hasNext = loadable,
    )

fun BookmarkContentResponse.toDomain(): BookmarkContent =
    BookmarkContent(
        bookmarkId = id,
        content = content.toDomain(),
        tripDuration = tripDuration.toDomain(),
        tripPlaceCount = tripPlaceCount,
        createdAt = createdAt,
    )

fun BookmarkCreationResponse.toDomain(trip: Trip): BookmarkContent =
    BookmarkContent(
        bookmarkId = id,
        content = content.toDomain(),
        tripDuration = trip.tripDuration,
        tripPlaceCount = trip.tripPlaceCount,
        createdAt = createdAt,
    )
