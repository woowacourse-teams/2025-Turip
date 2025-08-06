package com.on.turip.data.region.repository

import com.on.turip.data.region.datasource.RegionRemoteDataSource
import com.on.turip.data.region.toDomain
import com.on.turip.domain.region.RegionCategory
import com.on.turip.domain.region.repository.RegionRepository

class DefaultRegionRepository(
    private val regionRemoteDataSource: RegionRemoteDataSource,
) : RegionRepository {
    override suspend fun loadRegionCategories(isKorea: Boolean): Result<List<RegionCategory>> =
        regionRemoteDataSource.getRegionCategories(isKorea).mapCatching { it.toDomain() }
}
