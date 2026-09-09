package com.on.turip.feature.popularregion.impl.model

import androidx.compose.runtime.Immutable
import com.on.turip.feature.popularregion.impl.map.GeoPoint

/**
 * 지도에 칠할 열(heat) 한 덩어리.
 *
 * 화면은 방문자 수 원본을 알 필요가 없다. 상태에서 미리 0..1 로 정규화해 넘긴다.
 *
 * @param intensity 그 시점 최대 방문자 수 대비 비율 (0f..1f)
 * @param isLabeled 지도에 지역명을 적을 대상인지. 라벨이 겹치지 않도록 상위 몇 개만 true 다.
 */
@Immutable
data class RegionHeatPoint(
    val code: String,
    val name: String,
    val location: GeoPoint,
    val intensity: Float,
    val isLabeled: Boolean,
)
