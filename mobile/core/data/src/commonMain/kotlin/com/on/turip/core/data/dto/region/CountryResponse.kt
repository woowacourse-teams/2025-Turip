package com.on.turip.core.data.dto.region

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CountryResponse(
    @SerialName("countryImageUrl")
    val countryImageUrl: String,
    @SerialName("countryName")
    val countryName: String,
    @SerialName("id")
    val id: Int,
)
