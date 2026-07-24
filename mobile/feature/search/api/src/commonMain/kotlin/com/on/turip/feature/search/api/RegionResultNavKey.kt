package com.on.turip.feature.search.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class RegionResultNavKey(
    val regionCategoryName: String,
) : NavKey
