package com.on.turip.ui.common.model.turip

import androidx.compose.runtime.Immutable

@Immutable
data class TuripModel(
    val id: Long,
    val name: String,
    val placeCount: Int,
    val isSelected: Boolean,
    val isDefault: Boolean,
)
