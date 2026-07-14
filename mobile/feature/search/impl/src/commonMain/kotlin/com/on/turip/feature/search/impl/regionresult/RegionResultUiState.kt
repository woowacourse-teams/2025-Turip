package com.on.turip.feature.search.impl.regionresult

import androidx.compose.runtime.Immutable
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.feature.search.impl.model.VideoInformationModel
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
