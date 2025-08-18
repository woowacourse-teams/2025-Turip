package com.on.turip.ui.common.mapper

import com.on.turip.domain.folder.Folder
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderModel

fun Folder.toUiModel(folderId: Long): FavoritePlaceFolderModel =
    FavoritePlaceFolderModel(
        id = id,
        name = name,
        placeCount = placeCount,
        isSelected = id == folderId,
    )
