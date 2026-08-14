package com.on.turip.core.network.service

import com.on.turip.core.data.dto.region.RegionCategoriesResponse
import com.on.turip.core.data.dto.region.RegionCategoryResponse
import com.on.turip.core.network.ApiPath
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface RegionService {
    @GET(ApiPath.V1 + "region-categories")
    suspend fun getRegionCategories(
        @Query("isKorea") isDomestic: Boolean,
    ): RegionCategoriesResponse
}
