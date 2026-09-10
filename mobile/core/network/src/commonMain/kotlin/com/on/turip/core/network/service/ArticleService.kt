package com.on.turip.core.network.service

import com.on.turip.core.data.dto.article.ArticleDetailResponse
import com.on.turip.core.data.dto.article.ArticlesResponse
import com.on.turip.core.network.ApiPath
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface ArticleService {
    /**
     * @param lastId 커서 위치. 서버가 이 id 의 아티클이 실제로 있는지 검증하므로
     *  첫 페이지에서는 반드시 null 로 두어 파라미터 자체를 빼야 한다. 0 을 보내면 404 다.
     */
    @GET(ApiPath.V1 + "articles")
    suspend fun getArticles(
        @Query("size") size: Int,
        @Query("lastId") lastId: Long?,
    ): ArticlesResponse

    @GET(ApiPath.V1 + "articles/{articleId}")
    suspend fun getArticleDetail(
        @Path("articleId") articleId: Long,
    ): ArticleDetailResponse
}
