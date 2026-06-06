package com.on.turip.core.network.datasourceimpl

import com.on.turip.core.common.safeApiCall
import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.data.datasource.RegionRemoteDataSource
import com.on.turip.core.data.dto.region.RegionCategoriesResponse
import com.on.turip.core.network.service.RegionService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class DefaultRegionRemoteDataSource(
    private val regionService: RegionService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : RegionRemoteDataSource {
    override suspend fun getRegionCategories(isDomestic: Boolean): TuripResult<RegionCategoriesResponse> =
        withContext(coroutineContext) {
            safeApiCall { regionService.getRegionCategories(isDomestic) }
        }
}
