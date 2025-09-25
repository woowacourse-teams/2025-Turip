package com.on.turip.ui.main.favorite

import androidx.core.net.toUri
import com.on.turip.domain.favorite.FavoritePlace
import com.on.turip.domain.folder.FavoriteFolder
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderModel
import com.on.turip.ui.main.favorite.model.FavoritePlaceModel
import com.on.turip.ui.main.favorite.model.FavoritePlaceShareModel

fun FavoritePlace.toUiModel(): FavoritePlaceModel =
    FavoritePlaceModel(
        favoritePlaceId = id,
        order = order,
        placeId = place.placeId,
        name = place.name,
        uri = place.url.toUri(),
        category = place.category.joinToString(),
        isFavorite = true,
    )

fun FavoriteFolder.toUiModel(): FavoritePlaceFolderModel =
    FavoritePlaceFolderModel(
        id = id,
        name = name,
        placeCount = placeCount,
        isSelected = isFavorite,
    )

fun FavoritePlaceModel.toUiModel(): FavoritePlaceShareModel =
    FavoritePlaceShareModel(
        name = name,
        uri = uri,
    )
