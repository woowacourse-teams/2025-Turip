package com.on.turip.core.network.service

import com.on.turip.core.data.dto.region.RegionCategoriesResponse
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
}
