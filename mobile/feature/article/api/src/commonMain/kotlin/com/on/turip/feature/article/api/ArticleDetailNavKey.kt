package com.on.turip.feature.article.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * 매거진 아티클 상세.
 *
 * 아티클 본문은 페이로드가 커서 그대로 넘기지 않고 조회 키만 전달한다.
 *
 * @param articleId 아티클 식별자
 */
@Serializable
data class ArticleDetailNavKey(
    val articleId: Long,
) : NavKey
