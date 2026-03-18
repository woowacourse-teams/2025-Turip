package com.on.turip.ui.compose.search.regionresult.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class RegionResultNavKey(
    val regionCategoryName: String,
) : NavKey
