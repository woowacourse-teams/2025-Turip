package com.on.turip.domain.region.repository

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.domain.region.RegionCategory

interface RegionRepository {
    suspend fun loadRegionCategories(isDomestic: Boolean): TuripCustomResult<List<RegionCategory>>
}
