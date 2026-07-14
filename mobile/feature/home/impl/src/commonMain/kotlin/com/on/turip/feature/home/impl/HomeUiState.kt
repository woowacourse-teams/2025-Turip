package com.on.turip.feature.home.impl

import androidx.compose.runtime.Stable
import com.on.turip.core.model.region.RegionCategory
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.feature.home.impl.model.UsersLikeContentModel

@Stable
data class HomeUiState(
    val isLoading: Boolean,
    val regionCategories: List<RegionCategory>,
    val isDomesticSelected: Boolean,
    val usersLikeContents: List<UsersLikeContentModel>,
    val errorUiState: ErrorUiState,
) {
    companion object {
        val Idle: HomeUiState =
            HomeUiState(
                isLoading = false,
                regionCategories = emptyList(),
                isDomesticSelected = true,
                usersLikeContents = emptyList(),
                errorUiState = ErrorUiState.None,
            )
    }
}
