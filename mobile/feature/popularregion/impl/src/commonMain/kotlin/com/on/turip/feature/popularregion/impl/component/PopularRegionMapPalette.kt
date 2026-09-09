package com.on.turip.feature.popularregion.impl.component

import androidx.compose.ui.graphics.Color

/**
 * 지도 전용 색.
 *
 * 바다/땅/열 색은 이 화면에서만 쓰이므로 [com.on.turip.core.designsystem.theme.TuripColors]
 * 토큰을 늘리지 않고 여기에 모아 둔다. 지도 밖의 텍스트·카드·칩은 전부 테마 토큰을 쓴다.
 */
internal object PopularRegionMapPalette {
    val Sea = Color(0xFFBFE3EE)
    val Land = Color(0xFFFDF2F0)
    val LandLine = Color(0xFFE0D0CC)

    /** 범례 `많음` 쪽 끝 색 */
    val HeatHigh = Color(0xFFE23B3B)

    /** 범례 `적음` 쪽 끝 색 */
    val HeatLow = Color(0xFFFFF1EF)

    /** 지역 구획선. 땅 위에 얹히므로 흰빛으로 빼서 목업의 행정경계선처럼 보이게 한다. */
    val CellLine = Color(0x8CFFFFFF)

    /** 선택된 구획의 테두리 */
    val SelectedCellLine = Color(0xFFC22B2B)

    val Label = Color(0xFF4A4A4A)
    val SelectedLabel = Color(0xFFC22B2B)
}
