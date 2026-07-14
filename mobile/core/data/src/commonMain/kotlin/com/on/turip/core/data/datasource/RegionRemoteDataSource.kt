package com.on.turip.core.data.datasource

import com.on.turip.core.data.dto.region.RegionCategoriesResponse
import com.on.turip.core.model.result.TuripResult

interface RegionRemoteDataSource {
    suspend fun getRegionCategories(isDomestic: Boolean): TuripResult<RegionCategoriesResponse>
}
