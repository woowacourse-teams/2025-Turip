package com.on.turip.core.network.datasource

import com.on.turip.core.network.dto.bookmark.BookmarkAddRequest
import com.on.turip.core.network.dto.content.ContentResponse
import com.on.turip.core.network.service.BookmarkService

class DefaultBookmarkDatasource(
    private val bookmarkService: BookmarkService,
) : BookmarkDatasource {
    override suspend fun getBookmarks(size: Int, lastId: Long): List<ContentResponse> =
        bookmarkService.getBookmarks(size, lastId)

    override suspend fun addBookmark(contentId: Long) =
        bookmarkService.postBookmark(BookmarkAddRequest(contentId))

    override suspend fun removeBookmark(contentId: Long) =
        bookmarkService.deleteBookmark(contentId)
}
