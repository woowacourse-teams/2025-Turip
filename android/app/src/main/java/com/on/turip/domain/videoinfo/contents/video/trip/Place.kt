package com.on.turip.domain.videoinfo.contents.video.trip

data class Place(
    val placeId: Long,
    val name: String,
    val url: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val category: List<String>,
)
