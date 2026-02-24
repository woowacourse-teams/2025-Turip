package com.on.turip.data.bookmarks.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.data.bookmarks.dto.BookmarkAddRequest
import com.on.turip.data.bookmarks.dto.BookmarkContentsResponse
import com.on.turip.data.bookmarks.dto.BookmarkCountResponse
import com.on.turip.data.bookmarks.service.BookmarkService
import com.on.turip.data.result.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DefaultBookmarkRemoteDataSource @Inject constructor(
    private val bookmarkService: BookmarkService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : BookmarkRemoteDataSource {
    override suspend fun postBookmark(bookmarkAddRequest: BookmarkAddRequest): TuripResult<Unit> =
        withContext(coroutineContext) {
            safeApiCall { bookmarkService.postBookmark(bookmarkAddRequest) }
        }

    override suspend fun deleteBookmark(contentId: Long): TuripResult<Unit> =
        withContext(coroutineContext) {
            safeApiCall { bookmarkService.deleteBookmark(contentId) }
        }

    override suspend fun getBookmarks(
        size: Int,
        lastId: Long,
    ): TuripResult<BookmarkContentsResponse> =
        withContext(coroutineContext) {
            safeApiCall { bookmarkService.getBookmarks(size, lastId) }
        }

    override suspend fun getBookmarkCount(): TuripResult<BookmarkCountResponse> =
        withContext(coroutineContext) {
            safeApiCall { bookmarkService.getBookmarkCount() }
        }
}
