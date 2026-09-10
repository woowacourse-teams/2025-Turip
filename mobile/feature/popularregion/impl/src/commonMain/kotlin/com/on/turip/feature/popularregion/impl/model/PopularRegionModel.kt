package com.on.turip.feature.popularregion.impl.model

import androidx.compose.runtime.Immutable
import com.on.turip.core.ui.util.toVisitorCountText
import com.on.turip.feature.popularregion.impl.map.GeoPoint
import com.on.turip.feature.popularregion.impl.map.RegionShapeKey

/**
 * 지도에 찍히는 지역 한 곳.
 *
 * 지도는 두 층이라 이 모델도 두 종류가 섞여 있다.
 * - 시도 층: 방문자 수만 있고 콘텐츠가 없다. 국토를 빈 칸 없이 칠하는 배경이다.
 * - 인기 관광지 층: 순위와 지역 카테고리가 붙는다. 눌러서 연관 콘텐츠를 볼 수 있는 쪽이다.
 *
 * @param shapeKey 경계를 어디서 찾을지. 이 키가 층을 가른다.
 * @param name 지도 라벨과 시트 제목에 쓰는 짧은 이름 (`강원`, `강릉`)
 * @param rank 인기 관광지 안에서의 순위. 시도 층은 null 이다.
 * @param regionCategoryName 연관 콘텐츠를 조회할 지역 카테고리. 시도 층은 null 이다.
 */
@Immutable
data class PopularRegionModel(
    val shapeKey: RegionShapeKey,
    val name: String,
    val location: GeoPoint,
    val visitorCount: Long,
    val rank: Int? = null,
    val regionCategoryName: String? = null,
) {
    /** 지도의 선택 식별자. */
    val code: String = shapeKey.code

    /** 인기 관광지 층인지. 콘텐츠 조회와 색 결정이 층마다 다르다. */
    val isDestination: Boolean = shapeKey is RegionShapeKey.Destination

    /** "2847만" 처럼 만 단위로 줄여 표기한다. */
    val visitorCountText: String = visitorCount.toVisitorCountText()

    /** 연관 콘텐츠를 조회할 수 있는 지역인지. */
    val hasRegionCategory: Boolean = regionCategoryName != null
}
