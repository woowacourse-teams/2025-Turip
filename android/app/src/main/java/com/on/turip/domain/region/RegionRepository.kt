package com.on.turip.domain.region

interface RegionRepository {
    suspend fun loadRegionCategories(isKorea: Boolean): Result<List<RegionCategory>>
}
