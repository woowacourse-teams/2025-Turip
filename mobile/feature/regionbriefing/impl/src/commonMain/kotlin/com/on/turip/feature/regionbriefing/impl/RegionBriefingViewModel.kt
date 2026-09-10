package com.on.turip.feature.regionbriefing.impl

import androidx.lifecycle.viewModelScope
import com.on.turip.core.data.session.SessionManager
import com.on.turip.core.domain.repository.ContentRepository
import com.on.turip.core.domain.repository.RegionRepository
import com.on.turip.core.model.result.ErrorType
import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.ui.BaseViewModel
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.core.ui.error.UiError
import com.on.turip.core.ui.error.toUiError
import com.on.turip.core.ui.model.content.toVideoSummaryModel
import com.on.turip.core.ui.model.region.RelatedSpotsUiState
import com.on.turip.core.ui.model.region.toRelatedSpotModel
import com.on.turip.core.ui.util.toVisitorCountText
import io.github.aakira.napier.Napier
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * 인기 관광지 지도에서 고른 지역 한 곳의 브리핑.
 *
 * 지역명·방문자 수는 지도에서 그대로 받아 오므로 이 화면이 조회하는 것은 영상과 연관 관광지뿐이다.
 * 두 조회는 서로를 기다리지 않고 각자 자기 영역만 로딩/에러로 그린다.
 */
class RegionBriefingViewModel(
    private val contentRepository: ContentRepository,
    private val regionRepository: RegionRepository,
    private val sessionManager: SessionManager,
) : BaseViewModel<RegionBriefingIntent, RegionBriefingState, RegionBriefingEffect>(
        RegionBriefingState(),
    ) {
    private var videosJob: Job? = null
    private var relatedSpotsJob: Job? = null
    private var loadMoreVideosJob: Job? = null

    override fun onIntent(intent: RegionBriefingIntent) {
        when (intent) {
            is RegionBriefingIntent.ClickVideo -> {
                emitEffect(RegionBriefingEffect.NavigateToTripDetail(intent.contentId))
            }

            is RegionBriefingIntent.ClickRelatedSpot -> {
                clickRelatedSpot(intent.spotCategory)
            }

            RegionBriefingIntent.LoadMoreVideos -> {
                loadMoreVideos()
            }

            RegionBriefingIntent.RetryVideos -> {
                loadVideos()
            }

            RegionBriefingIntent.RetryRelatedSpots -> {
                loadRelatedSpots()
            }
        }
    }

    /**
     * 화면이 NavKey 값을 넘겨준다. 같은 지역으로 다시 들어오면 이미 받아둔 목록을 그대로 쓴다.
     */
    fun initRegion(
        regionCategoryName: String,
        visitorCount: Long,
        baseMonth: String?,
    ) {
        if (currentState.regionName == regionCategoryName && currentState.isVideoListFetched) return

        updateState {
            RegionBriefingState(
                regionName = regionCategoryName,
                visitorCountText = visitorCount.toVisitorCountText(),
                baseMonth = baseMonth,
            )
        }
        loadVideos()
        loadRelatedSpots()
    }

    private fun clickRelatedSpot(spotCategory: String) {
        val regionCategoryName: String = currentState.regionName
        if (regionCategoryName.isEmpty()) return

        emitEffect(
            RegionBriefingEffect.NavigateToRelatedSpotDetail(
                regionCategoryName = regionCategoryName,
                spotCategory = spotCategory,
            ),
        )
    }

    private fun loadVideos() {
        val regionCategoryName: String = currentState.regionName
        if (regionCategoryName.isEmpty()) return

        videosJob?.cancel()
        loadMoreVideosJob?.cancel()
        videosJob =
            viewModelScope.launch {
                updateState {
                    copy(isVideoListLoading = true, videoErrorUiState = ErrorUiState.None)
                }

                when (
                    val result =
                        contentRepository.loadContentsByRegion(
                            regionCategoryName = regionCategoryName,
                            size = VIDEO_PAGE_SIZE,
                            lastId = INITIAL_LAST_ID,
                        )
                ) {
                    is TuripResult.Success -> {
                        updateState {
                            copy(
                                videos = result.value.videos
                                    .map { it.toVideoSummaryModel() }
                                    .toImmutableList(),
                                isVideoListLoading = false,
                                isVideoListFetched = true,
                                isVideoListLoadable = result.value.loadable,
                            )
                        }
                        Napier.d("지역 브리핑 - 영상 ${result.value.videos.size}개 로드")
                    }

                    is TuripResult.Failure -> {
                        Napier.e("지역 브리핑 - 영상 조회 실패", result.cause)
                        handleVideoError(result)
                    }
                }
            }
    }

    /**
     * 이미 받아온 페이지 안의 영상은 화면(더보기 버튼)에서 로컬로 먼저 펼치고,
     * 그걸 다 펼친 뒤에도 서버에 더 있으면 이 함수로 다음 페이지를 이어붙인다.
     */
    private fun loadMoreVideos() {
        if (currentState.isLoadingMoreVideos || !currentState.isVideoListLoadable) return

        val regionCategoryName: String = currentState.regionName
        val lastId: Long = currentState.videos.lastOrNull()?.contentId ?: return

        loadMoreVideosJob =
            viewModelScope.launch {
                updateState { copy(isLoadingMoreVideos = true) }

                when (
                    val result =
                        contentRepository.loadContentsByRegion(
                            regionCategoryName = regionCategoryName,
                            size = VIDEO_PAGE_SIZE,
                            lastId = lastId,
                        )
                ) {
                    is TuripResult.Success -> {
                        updateState {
                            copy(
                                videos = (
                                    videos +
                                        result.value.videos.map { it.toVideoSummaryModel() }
                                ).toImmutableList(),
                                isVideoListLoadable = result.value.loadable,
                                isLoadingMoreVideos = false,
                            )
                        }
                        Napier.d("지역 브리핑 - 영상 ${result.value.videos.size}개 추가 로드")
                    }

                    is TuripResult.Failure -> {
                        Napier.e("지역 브리핑 - 영상 추가 조회 실패", result.cause)
                        updateState { copy(isLoadingMoreVideos = false) }
                    }
                }
            }
    }

    /**
     * 연관 관광지는 일부 지역만 지원한다. 나머지는 [ErrorType.Region.InvalidCategory] 로 내려오는
     * 정상 흐름이라 에러 화면을 띄우지 않되, 섹션이 통째로 사라지면 미구현과 구분되지 않으므로
     * 사유를 상태로 남긴다.
     */
    private fun loadRelatedSpots() {
        val regionCategoryName: String = currentState.regionName
        if (regionCategoryName.isEmpty()) return

        relatedSpotsJob?.cancel()
        relatedSpotsJob =
            viewModelScope.launch {
                updateState { copy(relatedSpotsUiState = RelatedSpotsUiState.Loading) }

                val relatedSpotsUiState: RelatedSpotsUiState =
                    when (val result = regionRepository.loadRelatedSpots(regionCategoryName)) {
                        is TuripResult.Success -> {
                            Napier.d("지역 브리핑 - 연관 관광지 ${result.value.size}개 카테고리 로드")
                            RelatedSpotsUiState.Success(
                                result.value
                                    .map { it.toRelatedSpotModel() }
                                    .toImmutableList(),
                            )
                        }

                        is TuripResult.Failure -> {
                            if (result.errorType == ErrorType.Region.InvalidCategory) {
                                Napier.d("지역 브리핑 - 연관 관광지 미지원 지역: $regionCategoryName")
                                RelatedSpotsUiState.Unsupported
                            } else {
                                Napier.w("지역 브리핑 - 연관 관광지 조회 실패: $regionCategoryName", result.cause)
                                RelatedSpotsUiState.Error
                            }
                        }
                    }

                updateState { copy(relatedSpotsUiState = relatedSpotsUiState) }
            }
    }

    /**
     * 방문자 수는 이미 받아온 값이라 계속 보여줄 수 있다. 영상 영역만 에러 + 재시도로 처리한다.
     */
    private fun handleVideoError(failure: TuripResult.Failure) {
        val videoError: ErrorUiState =
            when (failure.errorType.toUiError()) {
                UiError.Global.Network -> {
                    ErrorUiState.Network
                }

                UiError.Global.TokenExpired -> {
                    navigateToLogin()
                    return
                }

                else -> {
                    ErrorUiState.Server
                }
            }
        updateState {
            copy(
                isVideoListLoading = false,
                isVideoListFetched = true,
                videoErrorUiState = videoError,
            )
        }
    }

    private fun navigateToLogin() {
        viewModelScope.launch {
            sessionManager.switchToGuest()
            emitEffect(RegionBriefingEffect.NavigateToLogin)
        }
    }

    companion object {
        private const val VIDEO_PAGE_SIZE: Int = 20
        private const val INITIAL_LAST_ID: Long = 0L
    }
}
