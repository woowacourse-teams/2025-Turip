package com.on.turip.ui.main.favorite.model

import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.compose.turip.selection.model.TuripPlaceModel
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

    data class ShowError(
        val errorUiState: ErrorUiState,
        val retryAction: PlaceTuripSelectionRetryAction,
    ) : PlaceTuripSelectionUiEffect
}

sealed interface PlaceTuripSelectionRetryAction {
    data object LoadTurips : PlaceTuripSelectionRetryAction

    data class UpdateTurip(
        val turipModel: TuripModel,
    ) : PlaceTuripSelectionRetryAction

    data class LoadPlacesInTurip(
        val folderId: Long,
        val folderName: String,
    ) : PlaceTuripSelectionRetryAction

    data class UpdateReorderedPlaces(
        val reorderedPlaces: ImmutableList<TuripPlaceModel>,
    ) : PlaceTuripSelectionRetryAction
}
