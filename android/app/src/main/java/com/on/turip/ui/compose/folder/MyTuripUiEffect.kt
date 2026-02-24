package com.on.turip.ui.compose.folder

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

    data class AddMyTurip(
        val name: String,
    ) : MyTuripRetryAction
}
