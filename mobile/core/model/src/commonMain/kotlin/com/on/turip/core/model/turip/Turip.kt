package com.on.turip.core.model.turip

data class Turip(
    val id: Long,
    val name: String,
    val isDefault: Boolean,
    val placeCount: Int,
    val memberCount: Int,
    val isShared: Boolean,
    val hasIncludePlace: Boolean = false,
)
