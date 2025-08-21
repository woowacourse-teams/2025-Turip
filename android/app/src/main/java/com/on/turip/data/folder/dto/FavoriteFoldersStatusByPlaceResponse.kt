package com.on.turip.data.folder.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteFoldersStatusByPlaceResponse(
    @SerialName("favoriteFolders")
    val favoriteFolders: List<FavoriteFolderStatusByPlaceResponse>,
)
