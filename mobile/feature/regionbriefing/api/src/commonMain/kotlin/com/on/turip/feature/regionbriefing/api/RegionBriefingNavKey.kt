package com.on.turip.feature.regionbriefing.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * 지역 하나의 브리핑(방문자 수 + 영상 + 연관 관광지).
 *
 * 방문자 수는 화면에서 다시 조회하지 않고 그대로 받는다.
 * 인기 관광지 지도가 이미 갖고 있는 값이라, 넘겨받아야 지도 시트에 보이던 숫자와 어긋나지 않는다.
 *
 * @param regionCategoryName 여행지 지역명 (예: `강릉`). 시도명이 아니다.
 * @param visitorCount 기준월 방문자 수
 * @param baseMonth 방문자 수 기준월(`yyyyMM`). 서버에 수집된 데이터가 없으면 null 이다.
 */
@Serializable
data class RegionBriefingNavKey(
    val regionCategoryName: String,
    val visitorCount: Long,
    val baseMonth: String?,
) : NavKey
