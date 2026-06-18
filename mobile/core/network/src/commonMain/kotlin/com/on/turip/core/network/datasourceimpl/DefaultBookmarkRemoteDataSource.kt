package com.on.turip.core.network.datasourceimpl

import com.on.turip.core.common.safeApiCall
import com.on.turip.core.data.datasource.BookmarkRemoteDataSource
import com.on.turip.core.data.dto.bookmark.BookmarkAddRequest
import com.on.turip.core.data.dto.bookmark.BookmarkContentsResponse
import com.on.turip.core.data.dto.bookmark.BookmarkCountResponse
import com.on.turip.core.data.dto.bookmark.BookmarkCreationResponse
import com.on.turip.core.model.paging.Cursor
import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.network.service.BookmarkService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class DefaultBookmarkRemoteDataSource(
    private val bookmarkService: BookmarkService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : BookmarkRemoteDataSource {
    override suspend fun postBookmark(bookmarkAddRequest: BookmarkAddRequest): TuripResult<BookmarkCreationResponse> =
        withContext(coroutineContext) {
            safeApiCall { bookmarkService.postBookmark(bookmarkAddRequest) }
        }

    override suspend fun deleteBookmark(contentId: Long): TuripResult<Unit> =
        withContext(coroutineContext) {
            safeApiCall { bookmarkService.deleteBookmark(contentId) }
        }

    override suspend fun getBookmarks(cursor: Cursor): TuripResult<BookmarkContentsResponse> =
        withContext(coroutineContext) {
            safeApiCall { bookmarkService.getBookmarks(cursor.size, cursor.lastId ?: 0L) }
        }

    override suspend fun getBookmarkCount(): TuripResult<BookmarkCountResponse> =
        withContext(coroutineContext) {
            safeApiCall { bookmarkService.getBookmarkCount() }
        }
}
