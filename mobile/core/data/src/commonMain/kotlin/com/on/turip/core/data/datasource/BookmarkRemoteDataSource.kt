package com.on.turip.core.data.datasource

import com.on.turip.core.data.dto.bookmark.BookmarkAddRequest
import com.on.turip.core.data.dto.bookmark.BookmarkContentsResponse
import com.on.turip.core.data.dto.bookmark.BookmarkCountResponse
import com.on.turip.core.data.dto.bookmark.BookmarkCreationResponse
import com.on.turip.core.model.paging.Cursor
import com.on.turip.core.model.result.TuripResult

interface BookmarkRemoteDataSource {
    suspend fun postBookmark(bookmarkAddRequest: BookmarkAddRequest): TuripResult<BookmarkCreationResponse>

    suspend fun deleteBookmark(contentId: Long): TuripResult<Unit>

    suspend fun getBookmarks(cursor: Cursor): TuripResult<BookmarkContentsResponse>

    suspend fun getBookmarkCount(): TuripResult<BookmarkCountResponse>
}
