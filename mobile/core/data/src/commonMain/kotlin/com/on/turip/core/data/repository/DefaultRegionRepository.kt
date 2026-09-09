package com.on.turip.core.data.repository

import com.on.turip.core.data.datasource.RegionRemoteDataSource
import com.on.turip.core.data.mapper.toDomain
import com.on.turip.core.domain.repository.RegionRepository
import com.on.turip.core.model.region.RegionCategory
import com.on.turip.core.model.region.RegionPopularity
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

    /** 기준월이 한 달에 한 번만 넘어가므로 앱 수명 동안 한 번만 받아 온다. */
    private var cachedRegionPopularity: RegionPopularity? = null

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

    /**
     * 아직 수집된 데이터가 없으면 지역 목록이 비어 있는 채로 성공한다.
     * 비어 있는 응답은 캐싱하지 않아, 서버가 채워지면 다음 진입에서 바로 반영된다.
     */
    override suspend fun loadRegionPopularity(): TuripResult<RegionPopularity> {
        cachedRegionPopularity?.let { return TuripResult.Success(it) }

        return regionRemoteDataSource
            .getRegionPopularity()
            .mapCatching {
                val popularity: RegionPopularity = it.toDomain()
                if (popularity.regions.isNotEmpty()) {
                    cachedRegionPopularity = popularity
                }
                popularity
            }
    }
}
