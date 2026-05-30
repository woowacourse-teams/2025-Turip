package com.on.turip.core.domain.repository

interface RegionRepository {
    suspend fun loadRegionCategories(isDomestic: Boolean): Result<List<RegionCategory>>
}
