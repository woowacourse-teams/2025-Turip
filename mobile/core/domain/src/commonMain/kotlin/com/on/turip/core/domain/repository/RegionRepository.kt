package com.on.turip.core.domain.repository

import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.model.region.RegionCategory

interface RegionRepository {
    suspend fun loadRegionCategories(isDomestic: Boolean): TuripResult<List<RegionCategory>>
}
