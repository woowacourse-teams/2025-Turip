package com.on.turip.domain.turip

data class Turip(
    val id: Long,
    val name: String,
    val isDefault: Boolean,
    val placeCount: Int,
    val hasIncludePlace: Boolean = false,
)
