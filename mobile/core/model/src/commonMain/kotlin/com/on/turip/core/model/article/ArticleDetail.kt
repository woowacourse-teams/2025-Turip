package com.on.turip.core.model.article

import com.on.turip.core.model.trip.Place

/**
 * 아티클 상세.
 *
 * @param content 마크다운 원문. 이미지도 `![](url)` 형태로 본문 안에 들어 있다.
 * @param thumbnailUrl 서버가 비어 있는 썸네일을 기본 튜립 이미지로 채워 주므로 항상 값이 있다.
 * @param author 작성자. 탈퇴한 작성자의 아티클은 null 로 내려온다.
 * @param places "이 아티클의 장소" 섹션에 나열되는 장소들. 연관 장소가 없으면 빈 리스트다.
 */
data class ArticleDetail(
    val id: Long,
    val title: String,
    val subtitle: String,
    val content: String,
    val thumbnailUrl: String,
    val tags: List<String>,
    val author: ArticleAuthor?,
    val places: List<Place>,
    val createdAt: String,
    val updatedAt: String,
)
