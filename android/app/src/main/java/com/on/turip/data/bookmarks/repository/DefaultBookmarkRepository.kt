package com.on.turip.data.bookmarks.repository

import com.on.turip.core.result.TuripResult
import com.on.turip.core.result.mapCatching
import com.on.turip.data.bookmarks.datasource.BookmarkRemoteDataSource
import com.on.turip.data.bookmarks.toDomain
import com.on.turip.data.bookmarks.toRequestDto
import com.on.turip.domain.bookmark.PagedBookmarkContents
import com.on.turip.domain.bookmark.repository.BookmarkRepository
import javax.inject.Inject

class DefaultBookmarkRepository @Inject constructor(
    private val bookmarkRemoteDataSource: BookmarkRemoteDataSource,
) : BookmarkRepository {
    override suspend fun createBookmark(contentId: Long): TuripResult<Unit> =
        bookmarkRemoteDataSource.postBookmark(contentId.toRequestDto())

    override suspend fun deleteBookmark(contentId: Long): TuripResult<Unit> = bookmarkRemoteDataSource.deleteBookmark(contentId)

    override suspend fun loadBookmarks(
        size: Int,
        lastId: Long,
    ): TuripResult<PagedBookmarkContents> = bookmarkRemoteDataSource.getBookmarks(size, lastId).mapCatching { it.toDomain() }
}
