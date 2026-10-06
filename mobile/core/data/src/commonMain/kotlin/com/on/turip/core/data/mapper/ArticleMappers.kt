package com.on.turip.core.data.mapper

import com.on.turip.core.data.dto.article.ArticleAuthorResponse
import com.on.turip.core.data.dto.article.ArticleDetailResponse
import com.on.turip.core.data.dto.article.ArticleResponse
import com.on.turip.core.data.dto.article.ArticlesResponse
import com.on.turip.core.model.article.Article
import com.on.turip.core.model.article.ArticleAuthor
import com.on.turip.core.model.article.ArticleDetail
import com.on.turip.core.model.article.ArticlesResult

fun ArticlesResponse.toDomain(): ArticlesResult =
    ArticlesResult(
        articles = articles.map { it.toDomain() },
        loadable = loadable,
    )

fun ArticleResponse.toDomain(): Article =
    Article(
        id = id,
        title = title,
        subtitle = subtitle,
        tags = tags,
        thumbnailUrl = thumbnailUrl,
        author = author?.toDomain(),
        createdAt = createdAt,
    )

fun ArticleAuthorResponse.toDomain(): ArticleAuthor =
    ArticleAuthor(
        id = id,
        name = name,
    )

fun ArticleDetailResponse.toDomain(): ArticleDetail =
    ArticleDetail(
        id = id,
        title = title,
        subtitle = subtitle,
        content = content,
        thumbnailUrl = thumbnailUrl,
        tags = tags,
        author = author?.toDomain(),
        places = places.map { it.toDomain() },
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
