package com.on.turip.data.folder.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteFolderCreationResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("memberId")
    val memberId: Long,
    @SerialName("name")
    val name: String,
    @SerialName("isDefault")
    val isDefault: Boolean,
)
