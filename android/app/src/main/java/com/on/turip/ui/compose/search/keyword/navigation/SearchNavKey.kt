package com.on.turip.ui.compose.search.keyword.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class SearchNavKey(
    val keyword: String,
) : NavKey
