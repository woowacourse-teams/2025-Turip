package com.on.turip.data.region.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.data.result.safeApiCall
import com.on.turip.data.region.dto.RegionCategoriesResponse
import com.on.turip.data.region.service.RegionService
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultRegionRemoteDataSource @Inject constructor(
    private val regionService: RegionService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : RegionRemoteDataSource {
    override suspend fun getRegionCategories(isDomestic: Boolean): TuripResult<RegionCategoriesResponse> =
        withContext(coroutineContext) {
            safeApiCall { regionService.getRegionCategories(isDomestic) }
        }
}
