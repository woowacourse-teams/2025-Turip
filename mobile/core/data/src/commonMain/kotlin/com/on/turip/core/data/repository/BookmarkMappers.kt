package com.on.turip.core.data.repository

import com.on.turip.core.data.dto.bookmark.BookmarkAddRequest
import com.on.turip.core.data.dto.bookmark.BookmarkContentResponse
import com.on.turip.core.data.dto.bookmark.BookmarkContentsResponse
import com.on.turip.core.data.dto.bookmark.BookmarkCreationResponse
import com.on.turip.core.model.bookmark.Bookmark
import com.on.turip.core.model.bookmark.BookmarkContent
import com.on.turip.core.model.paging.Page

fun Long.toRequestDto(): BookmarkAddRequest = BookmarkAddRequest(contentId = this)

fun BookmarkCreationResponse.toDomain(): Bookmark =
    Bookmark(
        id = id,
        content = content.toDomain(),
        createdAt = createdAt,
    )

fun BookmarkContentResponse.toDomain(): BookmarkContent =
    BookmarkContent(
        bookmarkId = id,
        content = content.toDomain(),
        tripDuration = tripDuration.toDomain(),
        tripPlaceCount = tripPlaceCount,
        createdAt = createdAt,
    )

fun BookmarkContentsResponse.toDomain(): Page<BookmarkContent> =
    Page(
        items = bookmarks.map { it.toDomain() },
        hasNext = loadable,
    )
