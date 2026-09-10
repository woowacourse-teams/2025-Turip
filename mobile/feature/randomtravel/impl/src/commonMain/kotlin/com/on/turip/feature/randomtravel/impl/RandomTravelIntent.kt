package com.on.turip.feature.randomtravel.impl

import com.on.turip.core.ui.UiIntent

sealed interface RandomTravelIntent : UiIntent {
    /** 슬롯 회전이 끝까지 재생됨 */
    data object FinishSlot : RandomTravelIntent

    /** 배출구에 걸쳐 있는 티켓을 끝까지 당겨 뽑음 */
    data object PullTicket : RandomTravelIntent

    /** 지역 목록 조회 실패 후 재시도 */
    data object RetryDraw : RandomTravelIntent

    /** 브리핑 본문(영상 목록) 재시도 */
    data object RetryBriefing : RandomTravelIntent

    /** 연관 관광지 섹션 재시도 */
    data object RetryRelatedSpots : RandomTravelIntent

    /** 영상 목록 더보기 — 이미 받아온 페이지를 다 펼친 뒤에는 다음 페이지를 조회한다 */
    data object LoadMoreVideos : RandomTravelIntent

    data class SelectVideo(
        val contentId: Long,
    ) : RandomTravelIntent

    /** 선택한 영상의 장소를 담을 튜립 확인 시트를 연다 */
    data object ClickStartTrip : RandomTravelIntent

    data class ChangeTuripDraftName(
        val name: String,
    ) : RandomTravelIntent

    data class ToggleTuripDraftPlace(
        val placeId: Long,
    ) : RandomTravelIntent

    /** 담을 장소 전체 선택 / 전체 해제 */
    data object ToggleTuripDraftAllPlaces : RandomTravelIntent

    /** 튜립을 만들어 장소를 담고 영상으로 이동 */
    data object ConfirmTuripDraft : RandomTravelIntent

    /**
     * 결정을 미루고 영상부터 보러 간다.
     * 시트 상태는 그대로 남겨 둬서, 영상에서 뒤로 돌아오면 같은 시트가 이어서 뜬다.
     */
    data object WatchVideoAndReturn : RandomTravelIntent

    data object DismissTuripDraft : RandomTravelIntent

    /** 장소 조회 실패 후 재시도 */
    data object RetryTuripDraftPlaces : RandomTravelIntent

    data object ClickReroll : RandomTravelIntent
}
