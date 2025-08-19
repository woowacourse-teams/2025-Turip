package com.on.turip.data.place.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Place(
    @SerialName("address")
    val address: String?,
    @SerialName("categories")
    val categories: List<Category?>?,
    @SerialName("id")
    val id: Int?,
    @SerialName("latitude")
    val latitude: Double?,
    @SerialName("longitude")
    val longitude: Double?,
    @SerialName("name")
    val name: String?,
    @SerialName("url")
    val url: String?
)