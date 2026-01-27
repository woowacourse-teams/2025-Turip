package com.on.turip.domain.folder

class Turip(
    val id: Long,
    val name: String,
    val isDefault: Boolean,
    val isTuripPlace: Boolean,
    val placeCount: Int = 0,
)
