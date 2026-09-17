package com.on.turip.core.network.datasourceimpl

import com.on.turip.core.common.safeApiCall
import com.on.turip.core.data.datasource.ArticleRemoteDataSource
import com.on.turip.core.data.dto.article.ArticleDetailResponse
import com.on.turip.core.data.dto.article.ArticlesResponse
import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.network.service.ArticleService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class DefaultArticleRemoteDataSource(
    private val articleService: ArticleService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : ArticleRemoteDataSource {
    override suspend fun getArticles(
        size: Int,
        lastId: Long?,
    ): TuripResult<ArticlesResponse> =
        withContext(coroutineContext) {
            safeApiCall {
                articleService.getArticles(
                    size = size,
                    lastId = lastId,
                )
            }
        }

    override suspend fun getArticleDetail(articleId: Long): TuripResult<ArticleDetailResponse> =
        withContext(coroutineContext) {
            safeApiCall {
                articleService.getArticleDetail(articleId = articleId)
            }
        }
}
