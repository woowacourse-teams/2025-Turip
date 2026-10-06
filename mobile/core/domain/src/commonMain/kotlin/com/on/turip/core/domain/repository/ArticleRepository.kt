package com.on.turip.core.domain.repository

import com.on.turip.core.model.article.ArticleDetail
import com.on.turip.core.model.article.ArticlesResult
import com.on.turip.core.model.result.TuripResult

interface ArticleRepository {
    /**
     * 공개된 아티클 목록. 서버가 노출 순서로 정렬해 커서 단위로 내려준다.
     *
     * @param lastId 커서 위치. 첫 페이지는 null 이다.
     *  서버가 이 id 의 아티클 존재 여부를 검증하므로 0 을 보내면 404 로 떨어진다.
     */
    suspend fun loadArticles(
        size: Int = DEFAULT_ARTICLES_SIZE,
        lastId: Long? = null,
    ): TuripResult<ArticlesResult>

    suspend fun loadArticleDetail(articleId: Long): TuripResult<ArticleDetail>

    companion object {
        const val DEFAULT_ARTICLES_SIZE: Int = 10
    }
}
