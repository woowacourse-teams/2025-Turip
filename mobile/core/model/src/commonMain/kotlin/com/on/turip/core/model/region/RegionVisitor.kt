package com.on.turip.core.model.region

/**
 * 시도 한 곳의 방문 인원수.
 *
 * @param areaCode 법정동 시도 코드 (11 서울, 26 부산 …)
 * @param name 시도명. 서버가 방문자 수 API 응답값을 그대로 내려주므로 `서울특별시`, `강원특별자치도` 처럼 정식 명칭이다.
 * @param visitorCount 외지인 + 외국인 (현지인 제외)
 */
data class RegionVisitor(
    val areaCode: Int,
    val name: String,
    val visitorCount: Long,
)
