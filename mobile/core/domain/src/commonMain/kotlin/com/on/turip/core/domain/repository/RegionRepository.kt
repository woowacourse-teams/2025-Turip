package com.on.turip.core.domain.repository

import com.on.turip.core.model.region.RegionCategory
import com.on.turip.core.model.result.TuripResult

interface RegionRepository {
    suspend fun loadRegionCategories(isDomestic: Boolean): TuripResult<List<RegionCategory>>
}
