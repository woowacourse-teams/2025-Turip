package com.on.turip.data.bookmark.repository

import com.on.turip.core.result.TuripResult
import com.on.turip.core.result.mapCatching
import com.on.turip.data.bookmark.datasource.BookmarkRemoteDataSource
import com.on.turip.data.bookmark.toDomain
import com.on.turip.data.bookmark.toRequestDto
import com.on.turip.domain.bookmark.BookmarkContent
import com.on.turip.domain.bookmark.repository.BookmarkRepository
import com.on.turip.domain.common.paging.Page
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
    ): TuripResult<Page<BookmarkContent>> = bookmarkRemoteDataSource.getBookmarks(size, lastId).mapCatching { it.toDomain() }

    override suspend fun loadBookmarkCount(): TuripResult<Int> = bookmarkRemoteDataSource.getBookmarkCount().mapCatching { it.count }
}
