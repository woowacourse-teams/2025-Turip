package com.on.turip.ui.main.favorite.model

sealed interface FavoritePlaceUiEvent {
    data object ShowFolderShareNotAllowed : FavoritePlaceUiEvent
}
