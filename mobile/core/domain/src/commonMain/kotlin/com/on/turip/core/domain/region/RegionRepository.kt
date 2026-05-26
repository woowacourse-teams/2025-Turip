package com.on.turip.core.domain.region

import com.on.turip.core.model.RegionCategory

interface RegionRepository {
    suspend fun loadRegionCategories(isDomestic: Boolean): Result<List<RegionCategory>>
}
