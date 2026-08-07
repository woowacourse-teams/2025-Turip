package com.on.turip.feature.turip.impl

import com.on.turip.core.ui.error.ErrorUiModel

sealed interface MyTuripUiEffect {
    data object NavigateToLogin : MyTuripUiEffect

    data class TuripAdded(
        val turipName: String,
    ) : MyTuripUiEffect

    data class TuripDeleted(
        val turipName: String,
    ) : MyTuripUiEffect

    data class ShowError(
        val errorUiModel: ErrorUiModel?,
        val retryAction: MyTuripRetryAction,
    ) : MyTuripUiEffect

    data class ShowBottomSheetError(
        val errorUiModel: ErrorUiModel?,
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
