package com.on.turip.core.network.service

import com.on.turip.core.network.ApiPath
import com.on.turip.core.network.dto.bookmark.PostBookmarkRequest
import com.on.turip.core.network.dto.content.ContentResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface BookmarkService {
    @GET(ApiPath.V2 + "bookmarks")
    suspend fun getBookmarks(
        @Query("size") size: Int,
        @Query("lastId") lastId: Long,
    ): List<ContentResponse>

    @POST(ApiPath.V1 + "bookmarks")
    suspend fun postBookmark(
        @Body body: PostBookmarkRequest,
    )

    @DELETE(ApiPath.V1 + "bookmarks/{contentId}")
    suspend fun deleteBookmark(
        @Path("contentId") contentId: Long,
    )
}
