package com.on.turip.data.bookmarks.service

import com.on.turip.data.bookmarks.dto.BookmarkAddRequest
import com.on.turip.data.bookmarks.dto.BookmarkContentsResponse
import com.on.turip.data.bookmarks.dto.BookmarkCountResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query

interface BookmarkService {
    @POST("bookmarks")
    suspend fun postBookmark(
        @Body bookmarkAddRequest: BookmarkAddRequest,
    )

    @DELETE("bookmarks")
    suspend fun deleteBookmark(
        @Query("contentId") contentId: Long,
    )

    @GET("bookmarks")
    suspend fun getBookmarks(
        @Query("size") size: Int,
        @Query("lastId") lastId: Long,
    ): BookmarkContentsResponse

    @GET("bookmarks/count")
    suspend fun getBookmarkCount(): BookmarkCountResponse
}
