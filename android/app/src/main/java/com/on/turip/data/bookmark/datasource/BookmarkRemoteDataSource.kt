package com.on.turip.data.bookmark.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.data.bookmark.dto.BookmarkAddRequest
import com.on.turip.data.bookmark.dto.BookmarkContentsResponse
import com.on.turip.data.bookmark.dto.BookmarkCountResponse
import com.on.turip.data.bookmark.dto.BookmarkCreationResponse
import com.on.turip.domain.common.paging.Cursor

interface BookmarkRemoteDataSource {
    suspend fun postBookmark(bookmarkAddRequest: BookmarkAddRequest): TuripResult<BookmarkCreationResponse>

    suspend fun deleteBookmark(contentId: Long): TuripResult<Unit>

    suspend fun getBookmarks(cursor: Cursor): TuripResult<BookmarkContentsResponse>

    suspend fun getBookmarkCount(): TuripResult<BookmarkCountResponse>
}
