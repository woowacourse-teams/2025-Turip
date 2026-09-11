package com.on.turip.feature.article.impl.model

import androidx.compose.runtime.Immutable
import com.on.turip.core.model.article.ArticleDetail
import com.on.turip.core.model.trip.Place
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Immutable
data class ArticleDetailUiModel(
    val id: Long,
    val title: String,
    val subtitle: String,
    val content: String,
    val tags: ImmutableList<String>,
    val heroImageUrl: String,
    /**
     * 작성자 이름. 탈퇴한 작성자면 null 이며, 화면에서 "탈퇴한 사용자"로 대체해 그린다.
     */
    val authorName: String?,
    val publishedDate: String,
    val places: ImmutableList<ArticlePlaceUiModel>,
) {
    val tagsText: String = tags.joinToString(separator = " ") { "#$it" }
}

@Immutable
data class ArticlePlaceUiModel(
    val id: Long,
    val name: String,
    val address: String,
    val category: String,
    /**
     * 서버가 내려주는 장소 상세 링크. 장소명으로 검색을 조립하지 않으므로 동명 장소를 오탐하지 않는다.
     */
    val mapUrl: String,
)

fun ArticleDetail.toUiModel(): ArticleDetailUiModel =
    ArticleDetailUiModel(
        id = id,
        title = title,
        subtitle = subtitle,
        content = content,
        tags = tags.toImmutableList(),
        heroImageUrl = thumbnailUrl,
        authorName = author?.name,
        // 서버는 `2026-09-01T09:00:00` 형태로 내려준다. 목록 카드와 같은 방식으로 날짜만 쓴다.
        publishedDate = createdAt.substringBefore('T'),
        places = places.map { it.toUiModel() }.toImmutableList(),
    )

private fun Place.toUiModel(): ArticlePlaceUiModel =
    ArticlePlaceUiModel(
        id = placeId,
        name = name,
        address = address,
        // 카테고리는 `음식점 > 한식 > 육류,고기 > 닭요리` 처럼 계층으로 온다. 가장 구체적인 마지막 항목만 쓴다.
        category = category
            .firstOrNull()
            ?.substringAfterLast('>')
            ?.trim()
            .orEmpty(),
        mapUrl = url,
    )
