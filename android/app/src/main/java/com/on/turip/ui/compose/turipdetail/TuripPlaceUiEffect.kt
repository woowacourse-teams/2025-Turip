package com.on.turip.ui.compose.turipdetail

import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.compose.trip.turipselection.model.TuripPlaceModel
import com.on.turip.ui.compose.turipdetail.model.turip.TuripShareModel
import kotlinx.collections.immutable.ImmutableList

sealed interface TuripPlaceUiEffect {
    data object NavigateToLogin : TuripPlaceUiEffect

    data object ShowTuripShareNotAllowed : TuripPlaceUiEffect

    data object TuripUpdated : TuripPlaceUiEffect

    data object TuripDelete : TuripPlaceUiEffect

    data class ShowTuripPlaceRemoveFailed(
        val placeName: String,
    ) : TuripPlaceUiEffect

    data class ShowTuripPlaceRemoved(
        val placeName: String,
    ) : TuripPlaceUiEffect

    data class ShareTurip(
        val turipShareModel: TuripShareModel,
    ) : TuripPlaceUiEffect

    data class ShowError(
        val errorUiState: ErrorUiState,
        val retryAction: TuripPlaceRetryAction,
    ) : TuripPlaceUiEffect

    data class ShowReorderPlaceFailed(
        val retryAction: TuripPlaceRetryAction,
    ) : TuripPlaceUiEffect
}

sealed interface TuripPlaceRetryAction {
    data class UpdateTuripPlace(
        val placeId: Long,
    ) : TuripPlaceRetryAction

    data class UpdateReorderedPlaces(
        val reorderedPlaces: ImmutableList<TuripPlaceModel>,
    ) : TuripPlaceRetryAction

    data object TuripNameUpdate : TuripPlaceRetryAction

    data object TuripDelete : TuripPlaceRetryAction
}
