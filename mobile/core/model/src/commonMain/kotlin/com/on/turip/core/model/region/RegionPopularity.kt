package com.on.turip.core.model.region

/**
 * 최근 한 달(데이터가 존재하는 최신 완결 월) 기준 시도별 방문 인원수 집계.
 *
 * 튜립이 콘텐츠를 갖고 있지 않은 시도까지 17개 전부 내려온다. 히트맵을 빈 칸 없이 칠하기 위해서다.
 *
 * @param baseMonth 기준월(`yyyyMM`). 서버에 수집된 데이터가 없으면 null 이고, 이때 [regions] 도 비어 있다.
 * @param regions 방문 인원수 내림차순
 */
data class RegionPopularity(
    val baseMonth: String?,
    val regions: List<RegionVisitor>,
)
