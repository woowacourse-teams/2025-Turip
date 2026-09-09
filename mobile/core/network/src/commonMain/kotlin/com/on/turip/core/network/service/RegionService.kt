package com.on.turip.core.network.service

import com.on.turip.core.data.dto.region.RegionCategoriesResponse
import com.on.turip.core.data.dto.region.RegionPopularityResponse
import com.on.turip.core.data.dto.region.RelatedSpotsResponse
import com.on.turip.core.network.ApiPath
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface RegionService {
    @GET(ApiPath.V1 + "region-categories")
    suspend fun getRegionCategories(
        @Query("isKorea") isDomestic: Boolean,
    ): RegionCategoriesResponse

    /**
     * 서울 / 부산 / 제주 / 인천 / 대전 / 전주 / 강릉 / 속초 / 경주 만 지원한다.
     * 그 외 지역(`국내 기타` 포함)은 400 `REGION_CATEGORY_INVALID` 로 내려온다.
     */
    @GET(ApiPath.V1 + "related-spots")
    suspend fun getRelatedSpots(
        @Query("regionCategory") regionCategoryName: String,
    ): RelatedSpotsResponse

    /**
     * 최근 한 달(데이터가 존재하는 최신 완결 월) 기준 전체 시도(17개)의 방문 인원수를 많은 순으로 조회한다.
     *
     * 튜립이 지원하지 않는 시도까지 모두 내려온다. 히트맵을 빈 칸 없이 칠하기 위해서다.
     * 요청 파라미터가 없어 검증 오류(400)는 나지 않고, 수집된 데이터가 없으면 빈 목록으로 응답한다.
     */
    @GET(ApiPath.V1 + "regions/popularity")
    suspend fun getRegionPopularity(): RegionPopularityResponse
}
