package com.on.turip.data.folder.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteFolderAddRequest(
    @SerialName("name")
    val name: String,
)
