package com.on.turip.core.network.datasource

import com.on.turip.core.network.dto.region.RegionCategoryResponse

interface RegionDatasource {
    suspend fun getRegionCategories(isDomestic: Boolean): List<RegionCategoryResponse>
}
