package com.on.turip.core.data.repository

import com.on.turip.core.data.datasource.RegionRemoteDataSource
import com.on.turip.core.data.mapper.toDomain
import com.on.turip.core.domain.repository.RegionRepository
import com.on.turip.core.model.region.RegionCategory
import com.on.turip.core.model.region.RelatedSpotCategory
import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.model.result.mapCatching

class DefaultRegionRepository(
    private val regionRemoteDataSource: RegionRemoteDataSource,
) : RegionRepository {
    private val cachedDomesticRegionCategories: MutableList<RegionCategory> = mutableListOf()
    private val cachedAbroadRegionCategories: MutableList<RegionCategory> = mutableListOf()

    /** 관광지 목록은 거의 바뀌지 않고, 재추첨으로 같은 지역이 다시 나올 수 있어 지역 단위로 캐싱한다. */
    private val cachedRelatedSpots: MutableMap<String, List<RelatedSpotCategory>> = mutableMapOf()

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

    override suspend fun loadRelatedSpots(regionCategoryName: String): TuripResult<List<RelatedSpotCategory>> {
        cachedRelatedSpots[regionCategoryName]?.let { return TuripResult.Success(it) }

        return regionRemoteDataSource
            .getRelatedSpots(regionCategoryName)
            .mapCatching {
                val relatedSpots: List<RelatedSpotCategory> = it.toDomain()
                cachedRelatedSpots[regionCategoryName] = relatedSpots
                relatedSpots
            }
    }
}
