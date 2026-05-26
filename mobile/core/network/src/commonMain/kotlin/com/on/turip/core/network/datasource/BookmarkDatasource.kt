package com.on.turip.core.network.datasource

import com.on.turip.core.network.dto.content.ContentResponse

interface BookmarkDatasource {
    suspend fun getBookmarks(size: Int, lastId: Long): List<ContentResponse>
    suspend fun addBookmark(contentId: Long)
    suspend fun removeBookmark(contentId: Long)
}
