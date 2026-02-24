package com.on.turip.ui.main.favorite.model

import com.on.turip.ui.common.error.ErrorUiState

sealed interface TuripPlaceUiEffect {
    data object NavigateToLogin : TuripPlaceUiEffect

    data object ShowTuripShareNotAllowed : TuripPlaceUiEffect

    data object TuripUpdated : TuripPlaceUiEffect

    data object TuripDelete : TuripPlaceUiEffect

    data class ShareTurip(
        val turipShareModel: TuripShareModel,
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

    data object TuripNameUpdate : TuripPlaceRetryAction

    data object TuripDelete : TuripPlaceRetryAction
}
