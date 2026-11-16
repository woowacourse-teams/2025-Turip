package com.on.turip.feature.home.impl.model

/**
 * 홈 CTA 카드가 돌려 보여 주는 인기 관광지 한 칸.
 *
 * @param rank 1부터. 카드 좌상단 뱃지 숫자로 쓴다.
 * @param imageUrl 카드 배경. 지역 카테고리 목록에서 이름으로 찾아 붙이며, 아직 못 찾았으면 null 이다.
 * @param visitorCountText "2847만" 처럼 만 단위로 줄인 표기
 */
data class PopularDestinationModel(
    val rank: Int,
    val regionCategoryName: String,
    val imageUrl: String?,
    val visitorCountText: String,
)
