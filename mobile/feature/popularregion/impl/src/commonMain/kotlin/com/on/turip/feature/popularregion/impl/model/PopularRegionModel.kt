package com.on.turip.feature.popularregion.impl.model

import androidx.compose.runtime.Immutable
import com.on.turip.feature.popularregion.impl.map.GeoPoint
import kotlinx.collections.immutable.ImmutableList
import kotlin.math.round

/**
 * 지도에 찍히는 시도 한 곳.
 *
 * @param code 법정동 시도 코드를 문자열로 옮긴 값. 지도의 선택 식별자로 쓴다.
 * @param name 지도 라벨과 시트 제목에 쓰는 짧은 이름 (`강원`)
 * @param regionCategoryNames 이 시도에서 튜립이 콘텐츠를 갖고 있는 지역 카테고리 (`강원` → 강릉, 속초).
 * 비어 있으면 아직 튜립이 다루지 않는 지역이다.
 */
@Immutable
data class PopularRegionModel(
    val code: String,
    val name: String,
    val location: GeoPoint,
    val visitorCount: Long,
    val regionCategoryNames: ImmutableList<String>,
) {
    /** "2847만" 처럼 만 단위로 줄여 표기한다. */
    val visitorCountText: String = visitorCount.toManUnitText()

    /** 연관 콘텐츠를 조회할 수 있는 지역인지. */
    val hasRegionCategory: Boolean = regionCategoryNames.isNotEmpty()

    /**
     * `연관 콘텐츠 보기`가 향할 지역.
     *
     * 지역 결과 화면은 시도가 아니라 카테고리 한 곳만 받으므로, 카테고리가 여럿이면 첫 번째로 보낸다.
     */
    val primaryRegionCategoryName: String? = regionCategoryNames.firstOrNull()
}

private const val MAN: Double = 10_000.0

private fun Long.toManUnitText(): String {
    val man: Double = this / MAN
    val rounded: Double = round(man * 10) / 10
    val hasFraction: Boolean = rounded != round(rounded)
    return if (hasFraction) "${rounded}만" else "${rounded.toLong()}만"
}
