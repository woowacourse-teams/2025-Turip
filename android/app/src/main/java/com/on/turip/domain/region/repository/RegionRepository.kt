package com.on.turip.domain.region.repository

import com.on.turip.core.result.TuripResult
import com.on.turip.domain.region.RegionCategory

interface RegionRepository {
    suspend fun loadRegionCategories(isDomestic: Boolean): TuripResult<List<RegionCategory>>
}
