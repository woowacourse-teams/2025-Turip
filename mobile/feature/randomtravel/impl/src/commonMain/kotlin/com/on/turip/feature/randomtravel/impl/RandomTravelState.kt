package com.on.turip.feature.randomtravel.impl

import androidx.compose.runtime.Immutable
import com.on.turip.core.ui.UiState
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.core.ui.model.namestatus.TuripNameStatusModel
import com.on.turip.feature.randomtravel.impl.model.RandomDestinationModel
import com.on.turip.feature.randomtravel.impl.model.RandomTravelRelatedSpotModel
import com.on.turip.feature.randomtravel.impl.model.RandomTravelVideoModel
import com.on.turip.feature.randomtravel.impl.model.TuripDraftPlaceModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/**
 * 랜덤 여행은 한 화면에서 단계가 진행된다.
 * 추첨이 끝난 뒤에만 슬롯이 돌기 때문에 [Spinning] 시점에는 이미 결과가 확정돼 있다.
 */
@Immutable
sealed interface RandomTravelPhase {
    /** 지역 목록 조회 + 추첨. 결과를 모르는 채로 슬롯이 미리 돌고 있다(Idle). */
    data object Preparing : RandomTravelPhase

    /** 결과가 확정돼 당첨 칸으로 감속 정지 (2~3초). 탭하면 즉시 [Confirmed] 로 건너뛴다. */
    data object Spinning : RandomTravelPhase

    /** 여행지 확정. 배출구에서 티켓이 올라온다 (1.4초) */
    data object Confirmed : RandomTravelPhase

    /** 티켓 출력 + 여행 브리핑 */
    data object Briefing : RandomTravelPhase
}

/**
 * 연관 관광지 섹션의 상태.
 *
 * 서버가 일부 지역만 지원하고 조회도 실패할 수 있는데, 이때 섹션을 조용히 숨기면
 * `아직 구현되지 않은 것`과 구분할 수 없다. 그래서 사유를 상태로 구분해 항상 무언가를 그린다.
 */
@Immutable
sealed interface RelatedSpotsUiState {
    data object Loading : RelatedSpotsUiState

    data class Success(
        val relatedSpots: ImmutableList<RandomTravelRelatedSpotModel>,
    ) : RelatedSpotsUiState {
        val isEmpty: Boolean = relatedSpots.isEmpty()
    }

    /** 서버가 지원하지 않는 지역 (400 `REGION_CATEGORY_INVALID`) */
    data object Unsupported : RelatedSpotsUiState

    data object Error : RelatedSpotsUiState
}

/**
 * `이 여행 시작하기`를 누른 뒤 뜨는 확인 시트의 상태.
 *
 * 튜립 생성은 되돌리기 번거로운 동작이라 자동으로 만들지 않고, 이름과 담을 장소를 한 번 확인받는다.
 * 영상의 장소 목록은 브리핑에 없어서(영상 목록만 내려온다) 시트를 열 때 따로 조회한다.
 */
@Immutable
sealed interface TuripDraftUiState {
    data object Hidden : TuripDraftUiState

    /** 장소 목록 조회 중 */
    data object Loading : TuripDraftUiState

    data class Ready(
        val name: String,
        val nameStatus: TuripNameStatusModel,
        val places: ImmutableList<TuripDraftPlaceModel>,
        val isSaving: Boolean = false,
    ) : TuripDraftUiState {
        val selectedPlaceIds: ImmutableList<Long> =
            places.filter { it.isSelected }.map { it.placeId }.toImmutableList()

        val isAllSelected: Boolean = places.isNotEmpty() && selectedPlaceIds.size == places.size

        val canConfirm: Boolean =
            !isSaving && nameStatus.isConfirmEnabled && selectedPlaceIds.isNotEmpty()
    }

    /** 장소 조회 실패. 시트를 닫지 않고 재시도 / 영상만 보기를 고르게 한다. */
    data object Error : TuripDraftUiState
}

@Immutable
data class RandomTravelState(
    val phase: RandomTravelPhase = RandomTravelPhase.Preparing,
    val reelNames: ImmutableList<String> = persistentListOf(),
    val destination: RandomDestinationModel? = null,
    val videos: ImmutableList<RandomTravelVideoModel> = persistentListOf(),
    val relatedSpotsUiState: RelatedSpotsUiState = RelatedSpotsUiState.Loading,
    val selectedContentId: Long? = null,
    val isBriefingLoading: Boolean = false,
    val isBriefingFetched: Boolean = false,
    val isVideoListLoadable: Boolean = false,
    val isLoadingMoreVideos: Boolean = false,
    val errorUiState: ErrorUiState = ErrorUiState.None,
    val briefingErrorUiState: ErrorUiState = ErrorUiState.None,
    val turipDraftUiState: TuripDraftUiState = TuripDraftUiState.Hidden,
) : UiState {
    /** 슬롯 진입 전 단계의 에러. 이 경우 슬롯을 아예 보여주지 않는다. */
    val shouldShowErrorScreen: Boolean = errorUiState != ErrorUiState.None

    val shouldShowTicket: Boolean = phase == RandomTravelPhase.Briefing && destination != null

    val shouldShowEmptyBriefing: Boolean =
        isBriefingFetched &&
            videos.isEmpty() &&
            briefingErrorUiState == ErrorUiState.None

    val canStartTrip: Boolean = selectedContentId != null
}
