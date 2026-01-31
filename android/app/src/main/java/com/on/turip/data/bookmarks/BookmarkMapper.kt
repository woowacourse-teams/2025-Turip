package com.on.turip.data.bookmarks

import com.on.turip.data.bookmarks.dto.BookmarkAddRequest
import com.on.turip.data.bookmarks.dto.BookmarkContentResponse
import com.on.turip.data.bookmarks.dto.BookmarkContentsResponse
import com.on.turip.data.content.toDomain
import com.on.turip.domain.bookmark.BookmarkContent
import com.on.turip.domain.bookmark.PagedBookmarkContents

fun Long.toRequestDto(): BookmarkAddRequest = BookmarkAddRequest(contentId = this)

fun BookmarkContentsResponse.toDomain(): PagedBookmarkContents =
    PagedBookmarkContents(
        bookmarkContents = contents.map { it.toDomain() },
        loadable = loadable,
    )

fun BookmarkContentResponse.toDomain(): BookmarkContent =
    BookmarkContent(
        content = content.toDomain(),
        tripDuration = tripDuration.toDomain(),
        tripPlaceCount = tripPlaceCount,
    )
