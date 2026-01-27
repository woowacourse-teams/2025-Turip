package com.on.turip.data.bookmarks.service

import com.on.turip.data.bookmarks.dto.BookmarkAddRequest
import com.on.turip.data.bookmarks.dto.BookmarkContentsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface BookmarkService {
    @POST("bookmarks")
    suspend fun postBookmark(
        @Body bookmarkAddRequest: BookmarkAddRequest,
    ): Response<Unit>

    @DELETE("bookmarks")
    suspend fun deleteBookmark(
        @Query("contentId") contentId: Long,
    ): Response<Unit>

    @GET("bookmarks")
    suspend fun getBookmarks(
        @Query("size") size: Int,
        @Query("lastId") lastId: Long,
    ): Response<BookmarkContentsResponse>
}
