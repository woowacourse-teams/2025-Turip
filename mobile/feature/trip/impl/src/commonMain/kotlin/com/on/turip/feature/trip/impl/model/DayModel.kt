package com.on.turip.feature.trip.impl.model

import androidx.compose.runtime.Stable

@Stable
data class DayModel(
    val day: Int,
    val isSelected: Boolean = false,
) {
    companion object {
        const val ALL_PLACE = 0
    }
}
