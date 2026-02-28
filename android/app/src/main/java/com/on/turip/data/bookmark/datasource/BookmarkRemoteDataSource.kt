package com.on.turip.data.bookmark.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.data.bookmark.dto.BookmarkAddRequest
import com.on.turip.data.bookmark.dto.BookmarkContentsResponse
import com.on.turip.data.bookmark.dto.BookmarkCountResponse

interface BookmarkRemoteDataSource {
    suspend fun postBookmark(bookmarkAddRequest: BookmarkAddRequest): TuripResult<Unit>

    suspend fun deleteBookmark(contentId: Long): TuripResult<Unit>

    suspend fun getBookmarks(
        size: Int,
        lastId: Long,
    ): TuripResult<BookmarkContentsResponse>

    suspend fun getBookmarkCount(): TuripResult<BookmarkCountResponse>
}
