package com.on.turip.core.domain.repository

import com.on.turip.core.model.Content

interface BookmarkRepository {
    suspend fun getBookmarks(size: Int, lastId: Long): Result<List<Content>>

    suspend fun addBookmark(contentId: Long): Result<Unit>

    suspend fun removeBookmark(contentId: Long): Result<Unit>
}
