package com.on.turip.core.model.article

/**
 * 홈 매거진 섹션에 노출되는 아티클 요약 정보.
 *
 * 상세 화면에서 쓰는 본문 블록([ArticleDetail.blocks])은 담지 않는다.
 * 목록에서 본문까지 들고 다니면 카드 한 장당 페이로드가 지나치게 커지기 때문이다.
 *
 * @param thumbnailUrl 서버가 비어 있는 썸네일을 기본 튜립 이미지로 채워 주므로 항상 값이 있다.
 * @param author 작성자. 탈퇴한 작성자의 아티클은 null 로 내려온다.
 * @param createdAt 서버가 내려주는 일시 문자열 (예: `2026-09-01T10:00:00`).
 */
data class Article(
    val id: Long,
    val title: String,
    val subtitle: String,
    val tags: List<String>,
    val thumbnailUrl: String,
    val author: ArticleAuthor?,
    val createdAt: String,
)

data class ArticleAuthor(
    val id: Long,
    val name: String,
)
