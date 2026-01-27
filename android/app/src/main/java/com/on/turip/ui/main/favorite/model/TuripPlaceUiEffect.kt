package com.on.turip.ui.main.favorite.model

import com.on.turip.ui.common.error.ErrorUiState

sealed interface TuripPlaceUiEffect {
    data object NavigateToLogin : TuripPlaceUiEffect

    data object ShowFolderShareNotAllowed : TuripPlaceUiEffect

    data class ShareFolder(
        val favoriteFolderShareModel: FavoriteFolderShareModel,
    ) : TuripPlaceUiEffect

    data class ShowError(
        val errorUiState: ErrorUiState,
        val retryAction: TuripPlaceRetryAction,
    ) : TuripPlaceUiEffect
}

sealed interface TuripPlaceRetryAction {
    data class UpdateTuripPlace(
        val placeId: Long,
        val isTuripPlace: Boolean,
    ) : TuripPlaceRetryAction
}
