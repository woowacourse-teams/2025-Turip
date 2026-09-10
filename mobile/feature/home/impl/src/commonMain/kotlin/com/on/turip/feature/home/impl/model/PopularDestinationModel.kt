package com.on.turip.feature.home.impl.model

/**
 * 홈 CTA 에서 1초 간격으로 돌아가는 인기 관광지 한 칸.
 *
 * @param rank 1부터. 화면에는 뱃지 숫자로만 쓴다.
 * @param visitorCountText "2847만" 처럼 만 단위로 줄인 표기
 */
data class PopularDestinationModel(
    val rank: Int,
    val regionCategoryName: String,
    val visitorCountText: String,
)
