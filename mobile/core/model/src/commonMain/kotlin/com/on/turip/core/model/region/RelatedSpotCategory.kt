package com.on.turip.core.model.region

/**
 * 지역별 연관 관광지. 한국관광공사 OpenAPI 데이터를 서버가 카테고리(관광지/숙박/음식 등)로 묶어 내려준다.
 *
 * 카테고리 종류는 서버가 정하므로 클라이언트에서 열거하지 않는다.
 */
data class RelatedSpotCategory(
    val category: String,
    val spots: List<String>,
)
