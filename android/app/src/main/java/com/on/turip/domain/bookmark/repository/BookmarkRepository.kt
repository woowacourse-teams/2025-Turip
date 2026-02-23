package com.on.turip.domain.bookmark.repository

import com.on.turip.core.result.TuripResult
import com.on.turip.domain.bookmark.BookmarkContent
import com.on.turip.domain.common.paging.Page

interface BookmarkRepository {
    suspend fun createBookmark(contentId: Long): TuripResult<Unit>

    suspend fun deleteBookmark(contentId: Long): TuripResult<Unit>

    suspend fun loadBookmarks(
        size: Int,
        lastId: Long,
    ): TuripResult<Page<BookmarkContent>>
}
