package com.on.turip.ui.search.regionresult

import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.search.model.VideoInformationModel

sealed interface RegionResultUiState {
    data object Loading : RegionResultUiState

    data object Empty : RegionResultUiState

    data class Success(
        val videos: List<VideoInformationModel>,
        val totalCount: Int,
        val region: String,
    ) : RegionResultUiState

    data class Error(
        val errorUiState: ErrorUiState,
    ) : RegionResultUiState
}
