package com.on.turip.domain.videoinfo

data class Place(
    val placeId: Long,
    val name: String,
    val url: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val category: List<String>,
)
