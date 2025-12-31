package com.on.turip.data.region.repository

import com.on.turip.core.result.TuripResult
import com.on.turip.data.region.datasource.RegionRemoteDataSource
import com.on.turip.data.region.toDomain
import com.on.turip.data.result.mapCatching
import com.on.turip.domain.region.RegionCategory
import com.on.turip.domain.region.repository.RegionRepository
import javax.inject.Inject

class DefaultRegionRepository @Inject constructor(
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
