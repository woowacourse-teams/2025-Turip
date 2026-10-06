package com.on.turip.feature.popularregion.impl.map

import androidx.compose.runtime.Immutable

/** 지도 위의 한 점. 화면 좌표가 아니라 실제 위경도로만 표현한다. */
@Immutable
data class GeoPoint(
    val latitude: Double,
    val longitude: Double,
)
