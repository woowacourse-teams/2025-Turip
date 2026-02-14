package com.on.turip.ui.compose.trip.bottomsheet

import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.compose.trip.bottomsheet.model.TuripPlaceModel
import com.on.turip.ui.main.favorite.model.TuripModel
import com.on.turip.ui.main.favorite.model.TuripShareModel
import kotlinx.collections.immutable.ImmutableList

sealed interface PlaceTuripSelectionUiEffect {
    data object NavigateToLogin : PlaceTuripSelectionUiEffect

    data object TuripShareNotAllowed : PlaceTuripSelectionUiEffect

    data class ShowTuripPlaceRemoveFailed(
        val placeName: String,
    ) : PlaceTuripSelectionUiEffect

    data class ShareTurip(
        val turipShareModel: TuripShareModel,
    ) : PlaceTuripSelectionUiEffect

    data class ShowTuripPlaceRemoved(
        val placeName: String,
    ) : PlaceTuripSelectionUiEffect

    data class ShowReorderPlaceFailed(
        val retryAction: PlaceTuripSelectionRetryAction,
    ) : PlaceTuripSelectionUiEffect

    data class UpdateTuripsByPlace(
        val placeId: Long,
        val hasTurip: Boolean,
    ) : PlaceTuripSelectionUiEffect

    data class ShowError(
        val errorUiState: ErrorUiState,
        val retryAction: PlaceTuripSelectionRetryAction,
    ) : PlaceTuripSelectionUiEffect
}

sealed interface PlaceTuripSelectionRetryAction {
    data object UpdateTuripsByPlace : PlaceTuripSelectionRetryAction

    data class LoadTurips(
        val placeId: Long,
        val placeName: String,
    ) : PlaceTuripSelectionRetryAction

    data class UpdateTurip(
        val turipModel: TuripModel,
    ) : PlaceTuripSelectionRetryAction

    data class LoadPlacesInTurip(
        val turipId: Long,
        val turipName: String,
    ) : PlaceTuripSelectionRetryAction

    data class UpdateReorderedPlaces(
        val reorderedPlaces: ImmutableList<TuripPlaceModel>,
    ) : PlaceTuripSelectionRetryAction
}
