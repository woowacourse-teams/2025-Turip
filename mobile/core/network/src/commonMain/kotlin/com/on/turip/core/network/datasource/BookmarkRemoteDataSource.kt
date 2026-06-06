package com.on.turip.core.network.datasource

import com.on.turip.core.model.paging.Cursor
import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.network.dto.bookmark.BookmarkAddRequest
import com.on.turip.core.network.dto.bookmark.BookmarkContentsResponse
import com.on.turip.core.network.dto.bookmark.BookmarkCountResponse
import com.on.turip.core.network.dto.bookmark.BookmarkCreationResponse

interface BookmarkRemoteDataSource {
    suspend fun postBookmark(bookmarkAddRequest: BookmarkAddRequest): TuripResult<BookmarkCreationResponse>

    suspend fun deleteBookmark(contentId: Long): TuripResult<Unit>

    suspend fun getBookmarks(cursor: Cursor): TuripResult<BookmarkContentsResponse>

    suspend fun getBookmarkCount(): TuripResult<BookmarkCountResponse>
}
