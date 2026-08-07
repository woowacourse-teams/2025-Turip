package com.on.turip.feature.trip.impl.turipselection

import androidx.compose.runtime.Immutable

@Immutable
data class TuripSelectionModel(
    val id: Long,
    val name: String,
    val placeCount: Int,
    val isSelected: Boolean,
    val isDefault: Boolean,
)
