package com.on.turip.core.network.service

import com.on.turip.core.network.ApiPath
import com.on.turip.core.network.dto.region.RegionCategoryResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface RegionService {
    @GET(ApiPath.V1 + "region-categories")
    suspend fun getRegionCategories(
        @Query("isKorea") isDomestic: Boolean,
    ): List<RegionCategoryResponse>
}
