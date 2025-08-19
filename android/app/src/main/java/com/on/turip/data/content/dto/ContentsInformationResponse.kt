package com.on.turip.data.content.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContentsInformationResponse(
    @SerialName("contents")
    val contentsInformation: List<ContentInformationResponse>,
    @SerialName("loadable")
    val loadable: Boolean,
)
