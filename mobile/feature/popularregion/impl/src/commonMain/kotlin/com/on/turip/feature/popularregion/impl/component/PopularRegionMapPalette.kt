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

    /** 범례 `많음` 쪽 끝 색 */
    val HeatHigh = Color(0xFFE23B3B)

    /** 범례 `적음` 쪽 끝 색 */
    val HeatLow = Color(0xFFFFF1EF)

    /**
     * 방문자 수가 아직 오지 않은 지역의 땅 색.
     *
     * `적음`([HeatLow]) 과 헷갈리지 않도록 붉은 기를 뺐다. 값이 0 인 것과 값을 모르는 것은 다르다.
     */
    val UnknownLand = Color(0xFFEDEAE9)

    /** 시도 경계선. 칠해진 땅 위에 얹히므로 흰빛으로 빼서 색을 가리지 않게 한다. */
    val BoundaryLine = Color(0x8CFFFFFF)

    /** 선택된 지역의 테두리 */
    val SelectedBoundaryLine = Color(0xFFC22B2B)

    val Label = Color(0xFF4A4A4A)
    val SelectedLabel = Color(0xFFC22B2B)
}
