package com.on.turip.data.region.datasource

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.common.safeApiCall
import com.on.turip.data.region.dto.RegionCategoriesResponse
import com.on.turip.data.region.service.RegionService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DefaultRegionRemoteDataSource @Inject constructor(
    private val regionService: RegionService,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : RegionRemoteDataSource {
    override suspend fun getRegionCategories(isDomestic: Boolean): TuripCustomResult<RegionCategoriesResponse> =
        withContext(coroutineContext) {
            safeApiCall { regionService.getRegionCategories(isDomestic) }
        }
}
