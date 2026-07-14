package com.on.turip.core.domain.repository

import com.on.turip.core.model.bookmark.BookmarkContent
import com.on.turip.core.model.paging.Cursor
import com.on.turip.core.model.paging.Page
import com.on.turip.core.model.result.TuripResult
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.StateFlow

interface BookmarkRepository {
    val bookmarkContents: StateFlow<ImmutableList<BookmarkContent>>

    suspend fun createBookmark(contentId: Long): TuripResult<Unit>

    suspend fun deleteBookmark(contentId: Long): TuripResult<Unit>

    suspend fun loadBookmarks(cursor: Cursor): TuripResult<Page<BookmarkContent>>

    suspend fun loadBookmarkCount(): TuripResult<Int>

    fun clearCache()
}
