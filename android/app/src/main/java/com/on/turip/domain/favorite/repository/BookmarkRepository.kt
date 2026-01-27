package com.on.turip.domain.favorite.repository

import com.on.turip.core.result.TuripResult
import com.on.turip.domain.favorite.PagedBookmarkContents

interface BookmarkRepository {
    suspend fun createBookmark(contentId: Long): TuripResult<Unit>

    suspend fun deleteBookmark(contentId: Long): TuripResult<Unit>

    suspend fun loadBookmarks(
        size: Int,
        lastId: Long,
    ): TuripResult<PagedBookmarkContents>
}
