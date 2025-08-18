package com.on.turip.data.folder.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteFoldersResponse(
    @SerialName("favoriteFolders")
    val favoriteFolderResponses: List<FavoriteFolderResponse>,
)
