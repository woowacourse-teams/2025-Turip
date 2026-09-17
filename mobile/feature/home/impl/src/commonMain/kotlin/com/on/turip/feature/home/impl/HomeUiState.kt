package com.on.turip.feature.home.impl

import androidx.compose.runtime.Stable
import com.on.turip.core.model.region.RegionCategory
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.feature.home.impl.model.MagazineArticleModel
import com.on.turip.feature.home.impl.model.PopularDestinationModel
import com.on.turip.feature.home.impl.model.UsersLikeContentModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
data class HomeUiState(
    /** 인기 북마크 섹션만 따로 로딩한다. 화면 전체를 가리지 않고 해당 섹션 자리에 placeholder 를 그린다. */
    val isUsersLikeLoading: Boolean,
    /** 지역 카테고리 섹션만 따로 로딩한다. [isUsersLikeLoading] 과 같은 이유로 분리한다. */
    val isRegionsLoading: Boolean,
    val regionCategories: List<RegionCategory>,
    val isDomesticSelected: Boolean,
    val usersLikeContents: List<UsersLikeContentModel>,
    val errorUiState: ErrorUiState,
    /** 인기 관광지 CTA 카드가 돌려 보여 주는 Top 10. 조회에 실패하면 비어 있고, 그때는 카드가 빈 배경으로 남는다. */
    val popularDestinations: List<PopularDestinationModel> = emptyList(),
    val articles: ImmutableList<MagazineArticleModel> = persistentListOf(),
    val isArticlesLoading: Boolean = false,
) {
    companion object {
        /**
         * 초기 상태부터 로딩 중으로 둔다.
         * ViewModel init 에서 플래그를 올리기 전 첫 프레임에 빈 목록이 그려졌다가 placeholder 로 바뀌는 깜빡임을 막는다.
         */
        val Idle: HomeUiState =
            HomeUiState(
                isUsersLikeLoading = true,
                isRegionsLoading = true,
                regionCategories = emptyList(),
                isDomesticSelected = true,
                usersLikeContents = emptyList(),
                errorUiState = ErrorUiState.None,
                articles = persistentListOf(),
                isArticlesLoading = true,
            )
    }
}
