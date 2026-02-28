package com.on.turip.ui.compose.turip

import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.compose.turip.model.MyTuripModel

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

    data class DeleteMyTurip(
        val myTuripModel: MyTuripModel,
    ) : MyTuripRetryAction
}
