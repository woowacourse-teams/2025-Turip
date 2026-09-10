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

    /**
     * 인기 관광지의 테두리.
     *
     * 지도에서 **누를 수 있는 곳은 이 선이 둘린 곳뿐**이라는 표시다.
     * 시도는 칠하기만 하고 눌러도 반응하지 않으므로 흰 경계선([BoundaryLine])으로 남는다.
     *
     * 붉은 열 색 위에서 확실히 떠오르려면 이 정도로 진해야 한다.
     * 수원처럼 경기의 1% 남짓한 작은 지역도 이 선 덕분에 눈에 들어온다.
     */
    val SelectableBoundaryLine = Color(0xFF151515)

    val Label = Color(0xFF4A4A4A)
}
