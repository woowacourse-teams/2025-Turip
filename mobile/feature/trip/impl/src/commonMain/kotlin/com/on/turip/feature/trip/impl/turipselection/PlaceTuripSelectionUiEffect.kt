package com.on.turip.feature.trip.impl.turipselection

import com.on.turip.core.ui.error.ErrorUiState

sealed interface PlaceTuripSelectionUiEffect {
    data object NavigateToLogin : PlaceTuripSelectionUiEffect

    data object Dismiss : PlaceTuripSelectionUiEffect

    data class UpdateTuripsByPlace(
        val placeId: Long,
        val hasTurip: Boolean,
    ) : PlaceTuripSelectionUiEffect

    data class ShowError(
        val errorUiState: ErrorUiState,
        val retryAction: PlaceTuripSelectionRetryAction,
    ) : PlaceTuripSelectionUiEffect

    data class TuripAdded(
        val turipName: String,
    ) : PlaceTuripSelectionUiEffect
}

sealed interface PlaceTuripSelectionRetryAction {
    data class LoadTurips(
        val placeId: Long,
        val placeName: String,
    ) : PlaceTuripSelectionRetryAction

    data object UpdateTuripsByPlace : PlaceTuripSelectionRetryAction

    data object AddTurip : PlaceTuripSelectionRetryAction
}
