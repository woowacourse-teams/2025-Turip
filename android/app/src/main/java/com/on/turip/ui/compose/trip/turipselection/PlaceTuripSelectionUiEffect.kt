package com.on.turip.ui.compose.trip.turipselection

import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.compose.trip.turipselection.model.TuripPlaceModel
import com.on.turip.ui.compose.turipdetail.model.turip.TuripShareModel
import com.on.turip.ui.main.favorite.model.TuripModel
import kotlinx.collections.immutable.ImmutableList

sealed interface PlaceTuripSelectionUiEffect {
    data object NavigateToLogin : PlaceTuripSelectionUiEffect

    data object TuripShareNotAllowed : PlaceTuripSelectionUiEffect

    data object Dismiss : PlaceTuripSelectionUiEffect

    data class ShowTuripPlaceRemoveFailed(
        val placeName: String,
    ) : PlaceTuripSelectionUiEffect

    data class ShareTuripByText(
        val turipShareModel: TuripShareModel,
    ) : PlaceTuripSelectionUiEffect

    data class ShareTuripInvitationLink(
        val invitationLink: String,
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

    data class HasNoTuripsByPlace(
        val placeId: Long,
    ) : PlaceTuripSelectionUiEffect

    class ShowError(
        val errorUiState: ErrorUiState,
        val retryAction: PlaceTuripSelectionRetryAction,
    ) : PlaceTuripSelectionUiEffect
}

sealed interface PlaceTuripSelectionRetryAction {
    data object UpdateTuripsByPlace : PlaceTuripSelectionRetryAction

    data object ShareTuripInvitationLink : PlaceTuripSelectionRetryAction

    data class LoadTurips(
        val placeId: Long,
        val placeName: String,
    ) : PlaceTuripSelectionRetryAction

    data class UpdateTurip(
        val turipModel: TuripModel,
    ) : PlaceTuripSelectionRetryAction

    data class LoadPlacesInTurip(
        val turipModel: TuripModel,
    ) : PlaceTuripSelectionRetryAction

    data class UpdateReorderedPlaces(
        val reorderedPlaces: ImmutableList<TuripPlaceModel>,
    ) : PlaceTuripSelectionRetryAction
}
