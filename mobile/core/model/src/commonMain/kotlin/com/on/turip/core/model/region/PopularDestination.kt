package com.on.turip.core.model.region

/**
 * 최근 한 달(데이터가 존재하는 최신 완결 월) 기준, 튜립이 지원하는 지역 카테고리의 방문 인원수 순위.
 *
 * 시도 단위인 [RegionPopularity] 와 달리 후보가 튜립 지역 카테고리 14곳이고, 그중 상위 10곳만 내려온다.
 * 지도에서는 시도 위에 덮어 그리는 2층이 된다.
 *
 * @param baseMonth 기준월(`yyyyMM`). 서버에 수집된 데이터가 없으면 null 이고, 이때 [destinations] 도 비어 있다.
 * @param destinations 방문 인원수 내림차순
 */
data class PopularDestination(
    val baseMonth: String?,
    val destinations: List<DestinationVisitor>,
)

/**
 * 지역 카테고리 한 곳의 방문 인원수.
 *
 * 좌표도 지역 코드도 내려오지 않는다. 지도에 놓을 자리는 [regionCategoryName] 으로 클라이언트가 찾는다.
 *
 * @param rank 순위 (1부터)
 * @param regionCategoryName 지역 카테고리명. 콘텐츠 조회(`GET /contents?regionCategory=`)에 그대로 쓴다.
 * @param visitorCount 외지인 + 외국인 (현지인 제외)
 */
data class DestinationVisitor(
    val rank: Int,
    val regionCategoryName: String,
    val visitorCount: Long,
)
