package com.on.turip.data.region.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.data.region.dto.RegionCategoriesResponse

interface RegionRemoteDataSource {
    suspend fun getRegionCategories(isDomestic: Boolean): TuripResult<RegionCategoriesResponse>
}
