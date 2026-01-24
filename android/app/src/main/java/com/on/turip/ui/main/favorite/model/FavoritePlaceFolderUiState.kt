package com.on.turip.ui.main.favorite.model

import com.on.turip.ui.compose.favorite.model.FavoritePlaceModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class FavoritePlaceFolderUiState(
    val screenMode: FavoritePlaceFolderScreenMode,
    val placeName: String,
    val favoritePlaceFolders: ImmutableList<FavoritePlaceFolderModel>,
    val selectedFolderPlaces: ImmutableList<FavoritePlaceModel>,
    val isChanged: Boolean,
) {
    val hasFavoriteFolder: Boolean
        get() = favoritePlaceFolders.any { it.isSelected }

    companion object {
        val Idle: FavoritePlaceFolderUiState =
            FavoritePlaceFolderUiState(
                screenMode = FavoritePlaceFolderScreenMode.Folders,
                placeName = "",
                favoritePlaceFolders = persistentListOf(),
                selectedFolderPlaces = persistentListOf(),
                isChanged = false,
            )
    }
}

sealed interface FavoritePlaceFolderScreenMode {
    data object Folders : FavoritePlaceFolderScreenMode

    data class FolderDetail(
        val folderId: Long,
        val folderName: String,
    ) : FavoritePlaceFolderScreenMode
}
