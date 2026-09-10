package com.on.turip.feature.randomtravel.impl.relatedspot

import androidx.lifecycle.viewModelScope
import com.on.turip.core.data.session.SessionManager
import com.on.turip.core.domain.repository.RegionRepository
import com.on.turip.core.model.region.RelatedSpotCategory
import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.ui.BaseViewModel
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.core.ui.error.UiError
import com.on.turip.core.ui.error.toUiError
import io.github.aakira.napier.Napier
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * 연관 관광지 카테고리 하나의 전체 목록 화면.
 *
 * 브리핑에서 이미 받아둔 응답을 [RegionRepository] 가 지역 단위로 캐싱하고 있어
 * 화면 진입 시 네트워크를 다시 타지 않는 것이 보통이다.
 */
class RelatedSpotDetailViewModel(
    private val regionRepository: RegionRepository,
    private val sessionManager: SessionManager,
) : BaseViewModel<RelatedSpotDetailIntent, RelatedSpotDetailState, RelatedSpotDetailEffect>(
        RelatedSpotDetailState(),
    ) {
    private var loadJob: Job? = null

    override fun onIntent(intent: RelatedSpotDetailIntent) {
        when (intent) {
            RelatedSpotDetailIntent.Retry -> loadSpots()
        }
    }

    fun initRelatedSpot(
        regionCategoryName: String,
        spotCategory: String,
    ) {
        if (currentState.regionCategoryName == regionCategoryName &&
            currentState.spotCategory == spotCategory &&
            currentState.isFetched
        ) {
            return
        }

        updateState {
            RelatedSpotDetailState(
                regionCategoryName = regionCategoryName,
                spotCategory = spotCategory,
            )
        }
        loadSpots()
    }

    private fun loadSpots() {
        val regionCategoryName: String = currentState.regionCategoryName
        if (regionCategoryName.isEmpty()) return

        loadJob?.cancel()
        loadJob =
            viewModelScope.launch {
                updateState { copy(isLoading = true, errorUiState = ErrorUiState.None) }

                when (val result = regionRepository.loadRelatedSpots(regionCategoryName)) {
                    is TuripResult.Success -> {
                        handleLoadSuccess(result.value)
                    }

                    is TuripResult.Failure -> {
                        Napier.e("연관 관광지 상세 - 조회 실패: $regionCategoryName", result.cause)
                        handleLoadFailure(result)
                    }
                }
            }
    }

    private fun handleLoadSuccess(relatedSpots: List<RelatedSpotCategory>) {
        val spots: List<String> =
            relatedSpots
                .firstOrNull { it.category == currentState.spotCategory }
                ?.spots
                .orEmpty()

        updateState {
            copy(
                spots = spots.toImmutableList(),
                isLoading = false,
                isFetched = true,
            )
        }
        Napier.d("연관 관광지 상세 - ${currentState.spotCategory} ${spots.size}곳 로드")
    }

    private fun handleLoadFailure(failure: TuripResult.Failure) {
        val errorUiState: ErrorUiState =
            when (failure.errorType.toUiError()) {
                UiError.Global.Network -> {
                    ErrorUiState.Network
                }

                UiError.Global.Server -> {
                    ErrorUiState.Server
                }

                UiError.Global.TokenExpired -> {
                    navigateToLogin()
                    return
                }

                else -> {
                    ErrorUiState.Unexpected
                }
            }

        updateState {
            copy(
                isLoading = false,
                isFetched = true,
                errorUiState = errorUiState,
            )
        }
    }

    private fun navigateToLogin() {
        viewModelScope.launch {
            sessionManager.switchToGuest()
            emitEffect(RelatedSpotDetailEffect.NavigateToLogin)
        }
    }
}
