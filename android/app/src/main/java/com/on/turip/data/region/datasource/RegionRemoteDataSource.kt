package com.on.turip.data.region.datasource

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.region.dto.RegionCategoriesResponse

interface RegionRemoteDataSource {
    suspend fun getRegionCategories(isDomestic: Boolean): TuripCustomResult<RegionCategoriesResponse>
}
