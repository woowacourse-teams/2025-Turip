package com.on.turip.core.data.repository

import com.on.turip.core.data.datasource.ArticleRemoteDataSource
import com.on.turip.core.data.mapper.toDomain
import com.on.turip.core.domain.repository.ArticleRepository
import com.on.turip.core.model.article.ArticleDetail
import com.on.turip.core.model.article.ArticlesResult
import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.model.result.mapCatching

class DefaultArticleRepository(
    private val articleRemoteDataSource: ArticleRemoteDataSource,
) : ArticleRepository {
    override suspend fun loadArticles(
        size: Int,
        lastId: Long?,
    ): TuripResult<ArticlesResult> =
        articleRemoteDataSource
            .getArticles(size = size, lastId = lastId)
            .mapCatching { it.toDomain() }

    override suspend fun loadArticleDetail(articleId: Long): TuripResult<ArticleDetail> =
        articleRemoteDataSource
            .getArticleDetail(articleId = articleId)
            .mapCatching { it.toDomain() }
}
