package com.on.turip.ui.compose.home

import androidx.compose.runtime.Stable
import com.on.turip.domain.region.RegionCategory
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.main.home.model.UsersLikeContentModel

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
