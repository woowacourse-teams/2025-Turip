package com.on.turip.feature.randomtravel.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * 연관 관광지 카테고리 하나의 장소 전체 목록.
 *
 * 장소 목록을 그대로 넘기지 않고 조회 키만 전달한다.
 * `RegionRepository` 가 지역 단위로 캐싱하므로 재조회 비용은 없다.
 * 캐시는 메모리에만 있어 `RegionRepository` 인스턴스가 유지되는 동안에만 재사용된다.
 *
 * @param regionCategoryName 여행지 지역명 (예: `서울`)
 * @param spotCategory 관광지 카테고리 (예: `관광지`, `숙박`, `음식`)
 */
@Serializable
data class RelatedSpotDetailNavKey(
    val regionCategoryName: String,
    val spotCategory: String,
) : NavKey
