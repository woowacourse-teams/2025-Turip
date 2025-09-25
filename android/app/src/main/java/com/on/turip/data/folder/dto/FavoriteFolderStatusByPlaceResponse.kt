package com.on.turip.data.folder.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteFolderStatusByPlaceResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("isDefault")
    val isDefault: Boolean,
    @SerialName("isFavoritePlace")
    val isFavoritePlace: Boolean,
    @SerialName("memberId")
    val memberId: Long,
    @SerialName("name")
    val name: String,
)
