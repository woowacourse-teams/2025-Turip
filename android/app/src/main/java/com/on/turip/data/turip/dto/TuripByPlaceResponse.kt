package com.on.turip.data.turip.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TuripByPlaceResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("isDefault")
    val isDefault: Boolean,
    @SerialName("isTuripPlace")
    val isTuripPlace: Boolean,
    @SerialName("memberId")
    val memberId: Long,
    @SerialName("name")
    val name: String,
)
