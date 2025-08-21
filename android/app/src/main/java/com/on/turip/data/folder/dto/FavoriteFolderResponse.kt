package com.on.turip.data.folder.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteFolderResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("isDefault")
    val isDefault: Boolean,
    @SerialName("memberId")
    val memberId: Long,
    @SerialName("name")
    val name: String,
    @SerialName("placeCount")
    val placeCount: Int,
)
