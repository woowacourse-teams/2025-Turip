package com.on.turip.ui.compose.turip

import com.on.turip.ui.common.error.ErrorUiState

sealed interface MyTuripUiEffect {
    data object NavigateToLogin : MyTuripUiEffect

    data class TuripAdded(
        val turipName: String,
    ) : MyTuripUiEffect

    data class TuripDeleted(
        val turipName: String,
    ) : MyTuripUiEffect

    data class ShowError(
        val errorUiState: ErrorUiState,
        val retryAction: MyTuripRetryAction,
    ) : MyTuripUiEffect
}

sealed interface MyTuripRetryAction {
    data object UpdateMyTurip : MyTuripRetryAction

    data object AddMyTurip : MyTuripRetryAction

    data class DeleteMyTurip(
        val turipId: Long,
    ) : MyTuripRetryAction
}
