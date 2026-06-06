package com.on.turip.core.data.repository

import com.on.turip.core.data.datasource.RegionRemoteDataSource
import com.on.turip.core.domain.repository.RegionRepository
import com.on.turip.core.model.region.RegionCategory
import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.model.result.mapCatching

class DefaultRegionRepository(
    private val regionRemoteDataSource: RegionRemoteDataSource,
) : RegionRepository {
    private val cachedDomesticRegionCategories: MutableList<RegionCategory> = mutableListOf()
    private val cachedAbroadRegionCategories: MutableList<RegionCategory> = mutableListOf()

    override suspend fun loadRegionCategories(isDomestic: Boolean): TuripResult<List<RegionCategory>> {
        val cachedRegionCategories: MutableList<RegionCategory> =
            if (isDomestic) cachedDomesticRegionCategories else cachedAbroadRegionCategories

        if (cachedRegionCategories.isNotEmpty()) {
            return TuripResult.Success(cachedRegionCategories)
        }

        return regionRemoteDataSource
            .getRegionCategories(isDomestic)
            .mapCatching {
                val categories: List<RegionCategory> = it.toDomain()
                cachedRegionCategories.addAll(categories)
                categories
            }
    }
}
