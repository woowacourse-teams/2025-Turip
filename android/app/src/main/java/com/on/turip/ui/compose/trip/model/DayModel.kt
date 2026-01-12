package com.on.turip.ui.compose.trip.model

import androidx.compose.runtime.Stable

@Stable
data class DayModel(
    val day: Int,
    val isSelected: Boolean = false,
)
