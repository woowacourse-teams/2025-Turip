package com.on.turip.core.network.datasource

import com.on.turip.core.network.dto.region.RegionCategoryResponse
import com.on.turip.core.network.service.RegionService

class DefaultRegionDatasource(
    private val regionService: RegionService,
) : RegionDatasource {
    override suspend fun getRegionCategories(isDomestic: Boolean): List<RegionCategoryResponse> =
        regionService.getRegionCategories(isDomestic)
}
