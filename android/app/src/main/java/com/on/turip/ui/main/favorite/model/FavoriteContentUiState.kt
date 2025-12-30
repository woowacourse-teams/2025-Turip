package com.on.turip.ui.main.favorite.model

import com.on.turip.data.common.ErrorUiState
import com.on.turip.domain.favorite.FavoriteContent

data class FavoriteContentUiState(
    val isLoading: Boolean,
    val favoriteContents: List<FavoriteContent>,
    val errorUiState: ErrorUiState,
) {
    companion object {
        val Idle: FavoriteContentUiState =
            FavoriteContentUiState(
                isLoading = false,
                favoriteContents = emptyList(),
                errorUiState = ErrorUiState.None,
            )
    }
}
