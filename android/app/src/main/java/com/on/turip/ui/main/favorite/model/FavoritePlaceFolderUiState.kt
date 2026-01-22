package com.on.turip.ui.main.favorite.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class FavoritePlaceFolderUiState(
    val placeId: Long,
    val placeName: String,
    val favoritePlaceFolders: ImmutableList<FavoritePlaceFolderModel>,
    val isChanged: Boolean,
) {
    val hasFavoriteFolder: Boolean
        get() = favoritePlaceFolders.any { it.isSelected }

    companion object {
        val Idle: FavoritePlaceFolderUiState =
            FavoritePlaceFolderUiState(
                placeId = 0L,
                placeName = "",
                favoritePlaceFolders = persistentListOf(),
                isChanged = false,
            )
    }
}
