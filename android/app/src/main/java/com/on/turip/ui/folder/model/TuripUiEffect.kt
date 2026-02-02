package com.on.turip.ui.folder.model

import com.on.turip.ui.common.error.ErrorUiState

sealed interface TuripUiEffect {
    data object NavigateToLogin : TuripUiEffect

    data object TuripAdded : TuripUiEffect

    data object TuripUpdated : TuripUiEffect

    data object TuripDeleted : TuripUiEffect

    class ShowError(
        val errorUiState: ErrorUiState,
        val action: TuripRetryAction,
    ) : TuripUiEffect
}

sealed interface TuripRetryAction {
    data object TuripAdd : TuripRetryAction

    data object TuripNameUpdate : TuripRetryAction

    data object TuripDelete : TuripRetryAction
}
