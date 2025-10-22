package com.on.turip.data.folder.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteFolderPostRequest(
    @SerialName("name")
    val name: String,
)
