package com.on.turip.feature.article.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * 매거진 아티클 전체 목록. 홈 매거진 섹션의 `전체보기` 에서 진입한다.
 */
@Serializable
data object ArticleListNavKey : NavKey
