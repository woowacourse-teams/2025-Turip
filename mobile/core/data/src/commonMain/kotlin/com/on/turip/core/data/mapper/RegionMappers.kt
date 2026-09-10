package com.on.turip.core.data.mapper

import com.on.turip.core.data.dto.region.CountryResponse
import com.on.turip.core.data.dto.region.DestinationVisitorResponse
import com.on.turip.core.data.dto.region.PopularDestinationResponse
import com.on.turip.core.data.dto.region.RegionCategoriesResponse
import com.on.turip.core.data.dto.region.RegionCategoryResponse
import com.on.turip.core.data.dto.region.RegionPopularityResponse
import com.on.turip.core.data.dto.region.RegionVisitorResponse
import com.on.turip.core.data.dto.region.RelatedSpotResponse
import com.on.turip.core.data.dto.region.RelatedSpotsResponse
import com.on.turip.core.model.region.Country
import com.on.turip.core.model.region.DestinationVisitor
import com.on.turip.core.model.region.PopularDestination
import com.on.turip.core.model.region.RegionCategory
import com.on.turip.core.model.region.RegionPopularity
import com.on.turip.core.model.region.RegionVisitor
import com.on.turip.core.model.region.RelatedSpotCategory

fun RegionCategoriesResponse.toDomain(): List<RegionCategory> = regionCategories.map { it.toDomain() }

fun RegionCategoryResponse.toDomain(): RegionCategory =
    RegionCategory(
        name = regionCategoryName,
        imageUrl = regionCategoryImageUrl,
        country = country?.toDomain(),
    )

/** 장소가 하나도 없는 카테고리는 브리핑에 빈 카드로 남기지 않고 아예 버린다. */
fun RelatedSpotsResponse.toDomain(): List<RelatedSpotCategory> =
    relatedSpots
        .map { it.toDomain() }
        .filter { it.spots.isNotEmpty() }

fun RelatedSpotResponse.toDomain(): RelatedSpotCategory =
    RelatedSpotCategory(
        category = category,
        spots = spots,
    )

/**
 * 서버가 이미 방문 인원수 내림차순으로 내려주지만, 히트맵의 최댓값 계산이 첫 원소에 기대므로
 * 순서를 데이터 계층에서 한 번 더 보장한다.
 */
fun RegionPopularityResponse.toDomain(): RegionPopularity =
    RegionPopularity(
        baseMonth = baseMonth,
        regions =
            regions
                .map { it.toDomain() }
                .sortedByDescending { it.visitorCount },
    )

fun RegionVisitorResponse.toDomain(): RegionVisitor =
    RegionVisitor(
        areaCode = areaCode,
        name = regionName,
        visitorCount = visitorCount,
    )

/**
 * 서버가 순위 오름차순으로 주지만, 화면은 방문자 수 순서에 기대므로 여기서 한 번 더 정렬한다.
 */
fun PopularDestinationResponse.toDomain(): PopularDestination =
    PopularDestination(
        baseMonth = baseMonth,
        destinations =
            destinations
                .map { it.toDomain() }
                .sortedByDescending { it.visitorCount },
    )

fun DestinationVisitorResponse.toDomain(): DestinationVisitor =
    DestinationVisitor(
        rank = rank,
        regionCategoryName = regionCategory,
        visitorCount = visitorCount,
    )

fun CountryResponse.toDomain(): Country =
    Country(
        id = id,
        name = countryName,
        imageUrl = countryImageUrl,
    )
