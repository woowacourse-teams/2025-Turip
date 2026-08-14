package com.on.turip.feature.trip.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class TripDetailNavKey(
    val contentId: Long,
) : NavKey
