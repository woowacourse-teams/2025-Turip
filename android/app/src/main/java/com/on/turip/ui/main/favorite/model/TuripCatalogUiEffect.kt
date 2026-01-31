package com.on.turip.ui.main.favorite.model

import com.on.turip.ui.common.error.ErrorUiState

sealed interface TuripCatalogUiEffect {
    data object NavigateToLogin : TuripCatalogUiEffect

    data object ShowTuripShareNotAllowed : TuripCatalogUiEffect

    data class ShareTurip(
        val turipShareModel: TuripShareModel,
    ) : TuripCatalogUiEffect

    data class ShowError(
        val errorUiState: ErrorUiState,
        val action: TuripCatalogRetryAction,
    ) : TuripCatalogUiEffect
}

sealed interface TuripCatalogRetryAction {
    data object LoadPlacesInTurip : TuripCatalogRetryAction
}
