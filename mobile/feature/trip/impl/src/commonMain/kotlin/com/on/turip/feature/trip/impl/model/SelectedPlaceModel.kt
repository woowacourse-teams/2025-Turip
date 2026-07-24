package com.on.turip.feature.trip.impl.model

import androidx.compose.runtime.Immutable

@Immutable
data class SelectedPlaceModel(
    val placeId: Long,
    val placeName: String,
)
