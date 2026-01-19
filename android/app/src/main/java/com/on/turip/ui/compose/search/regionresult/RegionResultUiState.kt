package com.on.turip.ui.compose.search.regionresult

import androidx.compose.runtime.Immutable
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.compose.search.model.VideoInformationModel
import kotlinx.collections.immutable.ImmutableList

@Immutable
sealed interface RegionResultUiState {
    @Immutable
    data object Loading : RegionResultUiState

    @Immutable
    data object Empty : RegionResultUiState

    @Immutable
    data class Success(
        val videos: ImmutableList<VideoInformationModel>,
        val totalCount: Int,
        val region: String,
    ) : RegionResultUiState

    @Immutable
    data class Error(
        val errorUiState: ErrorUiState,
    ) : RegionResultUiState
}
