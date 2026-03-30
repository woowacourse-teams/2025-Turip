package com.on.turip.data.bookmark.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.core.result.mapCatching
import com.on.turip.data.bookmark.dto.BookmarkAddRequest
import com.on.turip.data.bookmark.dto.BookmarkContentsResponse
import com.on.turip.data.bookmark.dto.BookmarkCountResponse
import com.on.turip.data.bookmark.dto.BookmarkCreationResponse
import com.on.turip.data.bookmark.service.BookmarkService
import com.on.turip.data.result.safeApiCall
import com.on.turip.domain.common.paging.Cursor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DefaultBookmarkRemoteDataSource @Inject constructor(
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
