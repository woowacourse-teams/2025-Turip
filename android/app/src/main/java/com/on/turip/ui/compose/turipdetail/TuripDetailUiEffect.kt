package com.on.turip.ui.compose.turipdetail

import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.compose.trip.turipselection.model.TuripPlaceModel
import com.on.turip.ui.compose.turipdetail.model.turip.TuripShareModel
import kotlinx.collections.immutable.ImmutableList

sealed interface TuripDetailUiEffect {
    data object NavigateToLogin : TuripDetailUiEffect

    data object ShowTuripShareNotAllowed : TuripDetailUiEffect

    data object TuripUpdated : TuripDetailUiEffect

    data object TuripDelete : TuripDetailUiEffect

    data class ShowTuripDetailRemoveFailed(
        val placeName: String,
    ) : TuripDetailUiEffect

    data class ShowTuripDetailRemoved(
        val placeName: String,
    ) : TuripDetailUiEffect

    data class ShareTuripByText(
        val turipShareModel: TuripShareModel,
    ) : TuripDetailUiEffect

    data class ShareTuripInvitationLink(
        val invitationLink: String,
    ) : TuripDetailUiEffect

    data class ShowError(
        val errorUiState: ErrorUiState,
        val retryAction: TuripPlaceRetryAction,
    ) : TuripDetailUiEffect

    data class ShowReorderDetailFailed(
        val retryAction: TuripPlaceRetryAction,
    ) : TuripDetailUiEffect
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

    data object ShareTuripInvitationLink : TuripPlaceRetryAction

    data object StreamConnectionLost : TuripPlaceRetryAction
}
