package com.on.turip.core.data.datasource

import com.on.turip.core.data.dto.article.ArticleDetailResponse
import com.on.turip.core.data.dto.article.ArticlesResponse
import com.on.turip.core.model.result.TuripResult

interface ArticleRemoteDataSource {
    suspend fun getArticles(
        size: Int,
        lastId: Long?,
    ): TuripResult<ArticlesResponse>

    suspend fun getArticleDetail(articleId: Long): TuripResult<ArticleDetailResponse>
}
