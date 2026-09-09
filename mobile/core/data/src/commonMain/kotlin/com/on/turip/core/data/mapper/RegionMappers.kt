package com.on.turip.core.data.mapper

import com.on.turip.core.data.dto.region.CountryResponse
import com.on.turip.core.data.dto.region.RegionCategoriesResponse
import com.on.turip.core.data.dto.region.RegionCategoryResponse
import com.on.turip.core.data.dto.region.RelatedSpotResponse
import com.on.turip.core.data.dto.region.RelatedSpotsResponse
import com.on.turip.core.model.region.Country
import com.on.turip.core.model.region.RegionCategory
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

fun CountryResponse.toDomain(): Country =
    Country(
        id = id,
        name = countryName,
        imageUrl = countryImageUrl,
    )
