package com.on.turip.core.data.repository

import com.on.turip.core.data.dto.region.CountryResponse
import com.on.turip.core.data.dto.region.RegionCategoriesResponse
import com.on.turip.core.data.dto.region.RegionCategoryResponse
import com.on.turip.core.model.region.Country
import com.on.turip.core.model.region.RegionCategory

fun RegionCategoriesResponse.toDomain(): List<RegionCategory> = regionCategories.map { it.toDomain() }

fun RegionCategoryResponse.toDomain(): RegionCategory =
    RegionCategory(
        name = regionCategoryName,
        imageUrl = regionCategoryImageUrl,
        country = country?.toDomain(),
    )

fun CountryResponse.toDomain(): Country =
    Country(
        id = id,
        name = countryName,
        imageUrl = countryImageUrl,
    )
