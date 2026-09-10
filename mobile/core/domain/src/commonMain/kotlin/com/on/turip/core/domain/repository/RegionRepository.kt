package com.on.turip.core.domain.repository

import com.on.turip.core.model.region.PopularDestination
import com.on.turip.core.model.region.RegionCategory
import com.on.turip.core.model.region.RegionPopularity
import com.on.turip.core.model.region.RelatedSpotCategory
import com.on.turip.core.model.result.TuripResult

interface RegionRepository {
    suspend fun loadRegionCategories(isDomestic: Boolean): TuripResult<List<RegionCategory>>

    /**
     * 지역의 연관 관광지를 카테고리별로 조회한다.
     *
     * 서버가 지원하지 않는 지역이면 [com.on.turip.core.model.result.ErrorType.Region.InvalidCategory] 로 실패한다.
     */
    suspend fun loadRelatedSpots(regionCategoryName: String): TuripResult<List<RelatedSpotCategory>>

    /**
     * 최근 한 달 기준 시도별 방문 인원수를 많은 순으로 조회한다.
     *
     * 한 달에 한 번만 바뀌는 값이라 앱이 살아 있는 동안은 다시 요청하지 않는다.
     */
    suspend fun loadRegionPopularity(): TuripResult<RegionPopularity>

    suspend fun loadPopularDestinations(): TuripResult<PopularDestination>
}
