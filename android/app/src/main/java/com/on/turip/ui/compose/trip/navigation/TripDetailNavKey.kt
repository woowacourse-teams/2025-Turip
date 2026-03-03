package com.on.turip.ui.compose.trip.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class TripDetailNavKey(
    val contentId: Long,
) : NavKey
