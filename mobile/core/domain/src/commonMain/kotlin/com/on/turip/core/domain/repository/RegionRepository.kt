package com.on.turip.core.domain.repository

import com.on.turip.core.model.region.RegionCategory
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
}
