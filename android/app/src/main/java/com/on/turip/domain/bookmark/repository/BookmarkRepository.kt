package com.on.turip.domain.bookmark.repository

import com.on.turip.core.result.TuripResult
import com.on.turip.domain.bookmark.BookmarkContent
import com.on.turip.domain.common.paging.Cursor
import com.on.turip.domain.common.paging.Page
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.StateFlow

interface BookmarkRepository {
    val bookmarkContents: StateFlow<ImmutableList<BookmarkContent>>

    suspend fun createBookmark(contentId: Long): TuripResult<Unit>

    suspend fun deleteBookmark(contentId: Long): TuripResult<Unit>

    suspend fun loadBookmarks(cursor: Cursor): TuripResult<Page<BookmarkContent>>

    suspend fun loadBookmarkCount(): TuripResult<Int>
}
