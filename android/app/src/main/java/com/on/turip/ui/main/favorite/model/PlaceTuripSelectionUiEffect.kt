package com.on.turip.ui.main.favorite.model

import com.on.turip.ui.common.error.ErrorUiState

sealed interface PlaceTuripSelectionUiEffect {
    data object NavigateToLogin : PlaceTuripSelectionUiEffect

    data class ShowUpdateTurip(
        val turip: TuripModel,
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
}
