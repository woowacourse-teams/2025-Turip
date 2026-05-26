package com.on.turip.core.model

data class Place(
    val id: Long,
    val name: String,
    val category: String,
    val latitude: Double,
    val longitude: Double,
)
