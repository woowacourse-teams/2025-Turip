package com.on.turip.data.region.service

import com.on.turip.data.region.dto.RegionCategoriesResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface RegionService {
    @GET("region-categories")
    suspend fun getRegionCategories(
        @Query("isKorea") isDomestic: Boolean,
    ): RegionCategoriesResponse
}
