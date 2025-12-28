package com.on.turip.ui.search.keywordresult

import com.on.turip.data.common.ErrorUiState
import com.on.turip.ui.search.model.VideoInformationModel

sealed interface SearchUiState {
    data object Loading : SearchUiState

    data object Empty : SearchUiState

    data class Success(
        val videos: List<VideoInformationModel>,
        val totalCount: Int,
    ) : SearchUiState

    data class Error(
        val errorUiState: ErrorUiState,
    ) : SearchUiState
}
