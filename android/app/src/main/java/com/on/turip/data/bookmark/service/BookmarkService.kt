package com.on.turip.data.bookmark.service

import com.on.turip.core.network.ApiPath
import com.on.turip.data.bookmark.dto.BookmarkAddRequest
import com.on.turip.data.bookmark.dto.BookmarkCreationResponse
import com.on.turip.data.bookmark.dto.BookmarkContentsResponse
import com.on.turip.data.bookmark.dto.BookmarkCountResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query

interface BookmarkService {
    @POST(ApiPath.V1 + "bookmarks")
    suspend fun postBookmark(
        @Body bookmarkAddRequest: BookmarkAddRequest,
    ): BookmarkCreationResponse

    @DELETE(ApiPath.V1 + "bookmarks")
    suspend fun deleteBookmark(
        @Query("contentId") contentId: Long,
    )

    /**
     * @param size : 불러올 데이터 크기
     * @param lastId : 북마크 콘텐츠의 ID (콘텐츠 ID 아님!)
     */
    @GET(ApiPath.V2 + "bookmarks")
    suspend fun getBookmarks(
        @Query("size") size: Int,
        @Query("lastId") lastId: Long,
    ): BookmarkContentsResponse

    @GET(ApiPath.V1 + "bookmarks/count")
    suspend fun getBookmarkCount(): BookmarkCountResponse
}
