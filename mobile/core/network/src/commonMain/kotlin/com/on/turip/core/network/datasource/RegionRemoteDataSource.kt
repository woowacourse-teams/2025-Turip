package com.on.turip.core.network.datasource

import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.network.dto.region.RegionCategoriesResponse

interface RegionRemoteDataSource {
    suspend fun getRegionCategories(isDomestic: Boolean): TuripResult<RegionCategoriesResponse>
}
