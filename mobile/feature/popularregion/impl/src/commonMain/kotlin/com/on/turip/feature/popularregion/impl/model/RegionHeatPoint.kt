package com.on.turip.feature.popularregion.impl.model

import androidx.compose.runtime.Immutable
import com.on.turip.feature.popularregion.impl.map.GeoPoint
import com.on.turip.feature.popularregion.impl.map.RegionShapeKey

/**
 * 지도에 칠할 열(heat) 한 덩어리.
 *
 * 화면은 방문자 수 원본을 알 필요가 없다. 상태에서 미리 0..1 로 정규화해 넘긴다.
 * 두 층은 방문자 수의 자릿수가 다르므로 **층마다 따로** 정규화한다.
 * 시도 값으로 관광지를 칠하면 작은 시(속초)가 늘 가장 옅게 나와 순위를 읽을 수 없다.
 *
 * @param shapeKey 경계를 어디서 찾을지. 이 키가 층을 가르고, 층에 따라 색 띠가 달라진다.
 * @param intensity 같은 층의 최대 방문자 수 대비 비율 (0f..1f)
 * @param isLabeled 지도에 지역명을 적을 대상인지. 라벨이 겹치지 않도록 상위 몇 개만 true 다.
 */
@Immutable
data class RegionHeatPoint(
    val shapeKey: RegionShapeKey,
    val name: String,
    val location: GeoPoint,
    val intensity: Float,
    val isLabeled: Boolean,
) {
    val code: String = shapeKey.code

    val isDestination: Boolean = shapeKey is RegionShapeKey.Destination
}
