package com.on.turip.ui.compose.trip.model

import androidx.compose.runtime.Immutable

@Immutable
data class SelectedPlaceModel(
    val placeId: Long,
    val placeName: String,
)
