package com.on.turip.ui.search.keywordresult

import androidx.compose.runtime.Immutable
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.search.model.VideoInformationModel
import kotlinx.collections.immutable.ImmutableList

@Immutable
sealed interface SearchUiState {
    @Immutable
    data object Loading : SearchUiState

    @Immutable
    data class Empty(
        val keyword: String,
    ) : SearchUiState

    @Immutable
    data class Success(
        val videos: ImmutableList<VideoInformationModel>,
        val totalCount: Int,
    ) : SearchUiState

    @Immutable
    data class Error(
        val errorUiState: ErrorUiState,
    ) : SearchUiState
}
