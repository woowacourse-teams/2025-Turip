package com.on.turip.feature.home.impl

import androidx.compose.runtime.Stable
import com.on.turip.core.model.region.RegionCategory
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.feature.home.impl.model.PopularDestinationModel
import com.on.turip.feature.home.impl.model.UsersLikeContentModel

@Stable
data class HomeUiState(
    val isLoading: Boolean,
    val regionCategories: List<RegionCategory>,
    val isDomesticSelected: Boolean,
    val usersLikeContents: List<UsersLikeContentModel>,
    val errorUiState: ErrorUiState,
    /** 인기 관광지 CTA 가 돌려 보여 주는 Top 10. 조회에 실패하면 비어 있고, 그때는 CTA 아랫줄이 사라진다. */
    val popularDestinations: List<PopularDestinationModel> = emptyList(),
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
