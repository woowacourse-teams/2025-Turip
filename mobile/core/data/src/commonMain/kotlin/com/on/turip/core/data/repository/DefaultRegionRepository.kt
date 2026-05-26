package com.on.turip.core.data.repository

import com.on.turip.core.common.safeApiCall
import com.on.turip.core.domain.repository.RegionRepository
import com.on.turip.core.model.RegionCategory
import com.on.turip.core.network.datasource.RegionDatasource

class DefaultRegionRepository(
    private val regionDatasource: RegionDatasource,
) : RegionRepository {
    override suspend fun loadRegionCategories(isDomestic: Boolean): Result<List<RegionCategory>> =
        safeApiCall {
            regionDatasource.getRegionCategories(isDomestic).map {
                RegionCategory(name = it.name, imageUrl = it.imageUrl)
            }
        }
}