package com.on.turip.ui.main.favorite.model

import com.on.turip.ui.common.error.ErrorUiState

sealed interface FavoritePlaceUiEffect {
    data object NavigateToLogin : FavoritePlaceUiEffect

    data object ShowFolderShareNotAllowed : FavoritePlaceUiEffect

    data class ShareFolder(
        val turipShareModel: TuripShareModel,
    ) : FavoritePlaceUiEffect

    data class ShowError(
        val errorUiState: ErrorUiState,
        val retryAction: FavoritePlaceRetryAction,
    ) : FavoritePlaceUiEffect
}

sealed interface FavoritePlaceRetryAction {
    data class UpdateFavoritePlace(
        val placeId: Long,
        val isFavorite: Boolean,
    ) : FavoritePlaceRetryAction
}
