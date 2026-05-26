package com.on.turip.core.data.bookmark

import com.on.turip.core.common.safeApiCall
import com.on.turip.core.data.content.toDomain
import com.on.turip.core.domain.bookmark.BookmarkRepository
import com.on.turip.core.model.Content
import com.on.turip.core.network.datasource.BookmarkDatasource

class DefaultBookmarkRepository(
    private val bookmarkDatasource: BookmarkDatasource,
) : BookmarkRepository {
    override suspend fun getBookmarks(size: Int, lastId: Long): Result<List<Content>> =
        safeApiCall { bookmarkDatasource.getBookmarks(size, lastId).map { it.toDomain() } }

    override suspend fun addBookmark(contentId: Long): Result<Unit> =
        safeApiCall { bookmarkDatasource.addBookmark(contentId) }

    override suspend fun removeBookmark(contentId: Long): Result<Unit> =
        safeApiCall { bookmarkDatasource.removeBookmark(contentId) }
}
