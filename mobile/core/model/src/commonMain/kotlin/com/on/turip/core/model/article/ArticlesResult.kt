package com.on.turip.core.model.article

/**
 * 커서 기반으로 끊어 받은 아티클 한 페이지.
 *
 * 서버가 노출 순서(displayOrder)로 정렬해 주므로 클라이언트에서 다시 정렬하지 않는다.
 *
 * @param loadable 다음 페이지가 더 있는지. 다음 요청의 커서는 [articles] 마지막 항목의 id 다.
 */
data class ArticlesResult(
    val articles: List<Article>,
    val loadable: Boolean,
)
