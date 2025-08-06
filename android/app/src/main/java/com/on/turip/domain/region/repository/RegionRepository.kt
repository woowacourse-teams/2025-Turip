package com.on.turip.domain.region.repository

import com.on.turip.domain.region.RegionCategory

interface RegionRepository {
    suspend fun loadRegionCategories(isKorea: Boolean): Result<List<RegionCategory>>
}
