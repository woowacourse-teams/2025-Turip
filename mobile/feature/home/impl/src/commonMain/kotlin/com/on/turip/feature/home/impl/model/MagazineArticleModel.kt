package com.on.turip.feature.home.impl.model

import androidx.compose.runtime.Immutable
import com.on.turip.core.model.article.Article
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Immutable
data class MagazineArticleModel(
    val id: Long,
    val title: String,
    val subtitle: String,
    val tags: ImmutableList<String>,
    val thumbnailUrl: String,
    /**
     * 작성자 이름. 탈퇴한 작성자면 null 이며, 화면에서 "탈퇴한 사용자"로 대체해 그린다.
     */
    val authorName: String?,
    val publishedDate: String,
)

fun Article.toUiModel(): MagazineArticleModel =
    MagazineArticleModel(
        id = id,
        title = title,
        subtitle = subtitle,
        tags = tags.toImmutableList(),
        thumbnailUrl = thumbnailUrl,
        authorName = author?.name,
        // 서버는 `2026-09-01T10:00:00` 로 내려주는데 카드에는 날짜까지만 쓴다.
        publishedDate = createdAt.substringBefore('T'),
    )
