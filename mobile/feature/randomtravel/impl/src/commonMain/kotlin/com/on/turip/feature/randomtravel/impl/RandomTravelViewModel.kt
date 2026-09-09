package com.on.turip.feature.randomtravel.impl

import androidx.lifecycle.viewModelScope
import com.on.turip.core.data.session.SessionManager
import com.on.turip.core.domain.repository.ContentRepository
import com.on.turip.core.domain.repository.RegionRepository
import com.on.turip.core.domain.repository.TuripRepository
import com.on.turip.core.model.region.RegionCategory
import com.on.turip.core.model.result.ErrorType
import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.model.trip.ContentPlace
import com.on.turip.core.model.trip.Trip
import com.on.turip.core.model.turip.Turip
import com.on.turip.core.model.turip.TuripNameStatus
import com.on.turip.core.ui.BaseViewModel
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.core.ui.error.UiError
import com.on.turip.core.ui.error.toUiError
import com.on.turip.core.ui.model.namestatus.TuripNameStatusModel
import com.on.turip.core.ui.model.turip.TuripEditModel
import com.on.turip.feature.randomtravel.impl.model.RandomDestinationModel
import com.on.turip.feature.randomtravel.impl.model.TuripDraftPlaceModel
import com.on.turip.feature.randomtravel.impl.model.toTuripDraftUiModel
import com.on.turip.feature.randomtravel.impl.model.toUiModel
import io.github.aakira.napier.Napier
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RandomTravelViewModel(
    private val regionRepository: RegionRepository,
    private val contentRepository: ContentRepository,
    private val turipRepository: TuripRepository,
    private val sessionManager: SessionManager,
) : BaseViewModel<RandomTravelIntent, RandomTravelState, RandomTravelEffect>(RandomTravelState()) {
    /**
     * 같은 여행지가 연속으로 나오면 고장난 것처럼 보이므로 직전 결과는 후보에서 제외한다.
     * 재추첨 정책상 "오늘의 여행지"를 고정하지 않기 때문에 화면 수명 동안만 기억한다.
     */
    private var previousDestinationName: String? = null

    private var drawJob: Job? = null
    private var briefingJob: Job? = null
    private var relatedSpotsJob: Job? = null
    private var loadMoreVideosJob: Job? = null
    private var turipDraftJob: Job? = null

    init {
        drawDestination()
    }

    override fun onIntent(intent: RandomTravelIntent) {
        when (intent) {
            RandomTravelIntent.FinishSlot -> confirmDestination()
            RandomTravelIntent.PullTicket -> pullTicket()
            RandomTravelIntent.RetryDraw -> drawDestination()
            RandomTravelIntent.RetryBriefing -> retryBriefing()
            RandomTravelIntent.RetryRelatedSpots -> retryRelatedSpots()
            RandomTravelIntent.LoadMoreVideos -> loadMoreVideos()
            RandomTravelIntent.ClickReroll -> drawDestination()
            RandomTravelIntent.ClickStartTrip -> startTrip()
            RandomTravelIntent.RetryTuripDraftPlaces -> startTrip()
            is RandomTravelIntent.ChangeTuripDraftName -> changeTuripDraftName(intent.name)
            is RandomTravelIntent.ToggleTuripDraftPlace -> toggleTuripDraftPlace(intent.placeId)
            RandomTravelIntent.ToggleTuripDraftAllPlaces -> toggleTuripDraftAllPlaces()
            RandomTravelIntent.ConfirmTuripDraft -> confirmTuripDraft()
            RandomTravelIntent.WatchVideoAndReturn -> watchVideoAndReturn()
            RandomTravelIntent.DismissTuripDraft -> dismissTuripDraft()
            is RandomTravelIntent.SelectVideo -> selectVideo(intent.contentId)
        }
    }

    /**
     * 슬롯을 돌리면서 네트워크를 태우면 응답이 느릴 때 감속 연출이 끊긴다.
     * 지역 목록 조회 → 추첨까지 모두 끝난 뒤에 슬롯을 시작한다.
     *
     * `국내 기타`는 연관 관광지 등 다른 API에서 지원하지 않는 지역이라 뽑기 후보/릴에서 제외한다.
     */
    private fun drawDestination() {
        drawJob?.cancel()
        briefingJob?.cancel()
        relatedSpotsJob?.cancel()
        loadMoreVideosJob?.cancel()
        turipDraftJob?.cancel()
        drawJob =
            viewModelScope.launch {
                updateState { RandomTravelState(phase = RandomTravelPhase.Preparing) }

                val regions: List<RegionCategory> =
                    when (val result = regionRepository.loadRegionCategories(IS_DOMESTIC)) {
                        is TuripResult.Success ->
                            result.value.filterNot { it.name == DOMESTIC_ETC_REGION_NAME }
                        is TuripResult.Failure -> {
                            Napier.e("랜덤 여행 - 지역 목록 조회 실패", result.cause)
                            handlePreSpinError(result)
                            return@launch
                        }
                    }

                if (regions.isEmpty()) {
                    updateState { copy(errorUiState = ErrorUiState.Unexpected) }
                    return@launch
                }

                val candidates: List<RegionCategory> =
                    regions
                        .filterNot { it.name == previousDestinationName }
                        .ifEmpty { regions }

                val destination: RandomDestinationModel = pickDestination(candidates) ?: return@launch
                previousDestinationName = destination.name
                Napier.d("랜덤 여행 - 여행지 확정: ${destination.name}, 영상 ${destination.videoCount}개")

                updateState {
                    copy(
                        phase = RandomTravelPhase.Spinning,
                        reelNames = buildReelNames(regions, destination.name),
                        destination = destination,
                    )
                }

                loadBriefing(destination.name)
                loadRelatedSpots(destination.name)
            }
    }

    /**
     * 영상이 0건인 지역이 뽑히면 브리핑이 빈 화면이 되므로 콘텐츠 수를 확인하고 재추첨한다.
     * [MAX_DRAW_ATTEMPTS]회까지 모두 0건이면 마지막 후보를 그대로 사용하고 빈 상태 UI를 보여준다.
     *
     * @return 확정된 여행지. 조회에 실패해 추첨을 중단한 경우 null (에러 상태는 내부에서 반영한다)
     */
    private suspend fun pickDestination(candidates: List<RegionCategory>): RandomDestinationModel? {
        val remainCandidates: MutableList<RegionCategory> = candidates.toMutableList()
        var lastPicked: RandomDestinationModel? = null

        repeat(MAX_DRAW_ATTEMPTS) {
            val candidate: RegionCategory = remainCandidates.randomOrNull() ?: return lastPicked
            remainCandidates.remove(candidate)

            val videoCount: Int =
                when (val result = contentRepository.loadContentsSizeByRegion(candidate.name)) {
                    is TuripResult.Success -> result.value
                    is TuripResult.Failure -> {
                        Napier.e("랜덤 여행 - 콘텐츠 수 조회 실패: ${candidate.name}", result.cause)
                        handlePreSpinError(result)
                        return null
                    }
                }

            val picked: RandomDestinationModel =
                candidate.toUiModel(isDomestic = IS_DOMESTIC, videoCount = videoCount)
            if (videoCount > 0) return picked

            Napier.d("랜덤 여행 - 영상 0건으로 재추첨: ${candidate.name}")
            lastPicked = picked
        }
        return lastPicked
    }

    /**
     * 슬롯 릴에 흘러갈 지역명. 마지막 칸이 당첨 여행지가 되도록 구성한다.
     */
    private fun buildReelNames(
        regions: List<RegionCategory>,
        destinationName: String,
    ): ImmutableList<String> {
        val others: List<String> =
            regions
                .map { it.name }
                .filterNot { it == destinationName }
                .shuffled()

        if (others.isEmpty()) return List(REEL_SIZE) { destinationName }.toImmutableList()

        return (List(REEL_SIZE - 1) { index -> others[index % others.size] } + destinationName)
            .toImmutableList()
    }

    /**
     * 슬롯 회전이 끝까지 재생된 뒤(FinishSlot) 호출된다.
     *
     * 여기서 바로 브리핑으로 넘기지 않는다. 티켓이 배출구에 걸쳐 나온 채로 기다리다가,
     * 사용자가 [pullTicket] 으로 직접 뽑아야 다음 화면으로 넘어간다.
     */
    private fun confirmDestination() {
        if (currentState.phase != RandomTravelPhase.Spinning) return

        updateState { copy(phase = RandomTravelPhase.Confirmed) }
        emitEffect(RandomTravelEffect.PerformDestinationHaptic)
    }

    /** 배출구에 걸쳐 있는 티켓을 끝까지 당겨 뽑았다. */
    private fun pullTicket() {
        if (currentState.phase != RandomTravelPhase.Confirmed) return

        updateState { copy(phase = RandomTravelPhase.Briefing) }
        emitEffect(RandomTravelEffect.PerformDestinationHaptic)
    }

    private fun retryBriefing() {
        val destinationName: String = currentState.destination?.name ?: return
        loadBriefing(destinationName)
    }

    private fun loadBriefing(regionCategoryName: String) {
        briefingJob?.cancel()
        loadMoreVideosJob?.cancel()
        briefingJob =
            viewModelScope.launch {
                updateState {
                    copy(isBriefingLoading = true, briefingErrorUiState = ErrorUiState.None)
                }

                when (
                    val result =
                        contentRepository.loadContentsByRegion(
                            regionCategoryName = regionCategoryName,
                            size = BRIEFING_PAGE_SIZE,
                            lastId = INITIAL_LAST_ID,
                        )
                ) {
                    is TuripResult.Success -> {
                        updateState {
                            copy(
                                videos = result.value.videos
                                    .map { it.toUiModel() }
                                    .toImmutableList(),
                                isBriefingLoading = false,
                                isBriefingFetched = true,
                                isVideoListLoadable = result.value.loadable,
                            )
                        }
                        Napier.d("랜덤 여행 - 브리핑 영상 ${result.value.videos.size}개 로드")
                    }

                    is TuripResult.Failure -> {
                        Napier.e("랜덤 여행 - 브리핑 영상 조회 실패", result.cause)
                        handleBriefingError(result)
                    }
                }
            }
    }

    /**
     * 이미 받아온 페이지 안의 영상은 화면(더보기 버튼)에서 로컬로 먼저 펼치고,
     * 그걸 다 펼친 뒤에도 서버에 더 있으면([RandomTravelState.isVideoListLoadable]) 이 함수로 다음 페이지를 이어붙인다.
     */
    private fun loadMoreVideos() {
        if (currentState.isLoadingMoreVideos || !currentState.isVideoListLoadable) return

        val regionCategoryName: String = currentState.destination?.name ?: return
        val lastId: Long = currentState.videos.lastOrNull()?.contentId ?: return

        loadMoreVideosJob =
            viewModelScope.launch {
                updateState { copy(isLoadingMoreVideos = true) }

                when (
                    val result =
                        contentRepository.loadContentsByRegion(
                            regionCategoryName = regionCategoryName,
                            size = BRIEFING_PAGE_SIZE,
                            lastId = lastId,
                        )
                ) {
                    is TuripResult.Success -> {
                        updateState {
                            copy(
                                videos = (videos + result.value.videos.map { it.toUiModel() }).toImmutableList(),
                                isVideoListLoadable = result.value.loadable,
                                isLoadingMoreVideos = false,
                            )
                        }
                        Napier.d("랜덤 여행 - 브리핑 영상 ${result.value.videos.size}개 추가 로드")
                    }

                    is TuripResult.Failure -> {
                        Napier.e("랜덤 여행 - 브리핑 영상 추가 조회 실패", result.cause)
                        updateState { copy(isLoadingMoreVideos = false) }
                    }
                }
            }
    }

    /**
     * 연관 관광지는 일부 지역(서울/부산/제주/인천/대전/전주/강릉/속초/경주)만 지원한다.
     * 나머지 지역은 [ErrorType.Region.InvalidCategory] 로 내려오는 정상 흐름이라 에러 화면을 띄우지 않되,
     * 섹션이 통째로 사라지면 미구현과 구분되지 않으므로 사유를 상태로 남긴다.
     */
    private fun loadRelatedSpots(regionCategoryName: String) {
        relatedSpotsJob?.cancel()
        relatedSpotsJob =
            viewModelScope.launch {
                updateState { copy(relatedSpotsUiState = RelatedSpotsUiState.Loading) }

                val relatedSpotsUiState: RelatedSpotsUiState =
                    when (val result = regionRepository.loadRelatedSpots(regionCategoryName)) {
                        is TuripResult.Success -> {
                            Napier.d("랜덤 여행 - 연관 관광지 ${result.value.size}개 카테고리 로드")
                            RelatedSpotsUiState.Success(
                                result.value
                                    .map { it.toUiModel() }
                                    .toImmutableList(),
                            )
                        }

                        is TuripResult.Failure ->
                            if (result.errorType == ErrorType.Region.InvalidCategory) {
                                Napier.d("랜덤 여행 - 연관 관광지 미지원 지역: $regionCategoryName")
                                RelatedSpotsUiState.Unsupported
                            } else {
                                Napier.w("랜덤 여행 - 연관 관광지 조회 실패: $regionCategoryName", result.cause)
                                RelatedSpotsUiState.Error
                            }
                    }

                updateState { copy(relatedSpotsUiState = relatedSpotsUiState) }
            }
    }

    private fun retryRelatedSpots() {
        val destinationName: String = currentState.destination?.name ?: return
        loadRelatedSpots(destinationName)
    }

    private fun selectVideo(contentId: Long) {
        updateState {
            copy(selectedContentId = contentId.takeIf { it != selectedContentId })
        }
    }

    /**
     * 예전에는 곧장 영상으로 이동했지만, 지금은 영상의 장소를 담을 튜립을 먼저 제안한다.
     *
     * 튜립은 게스트도 device-fid 기준으로 가질 수 있으므로 세션과 무관하게 제안한다.
     * 담을 장소가 없는 영상은 제안할 것이 없으므로 예전처럼 바로 영상으로 이동한다.
     */
    private fun startTrip() {
        val contentId: Long = currentState.selectedContentId ?: return

        turipDraftJob?.cancel()
        turipDraftJob =
            viewModelScope.launch {
                updateState { copy(turipDraftUiState = TuripDraftUiState.Loading) }

                // 이름 중복은 서버도 막지만, 미리 채워 줄 이름을 겹치지 않게 지으려면 목록이 필요하다.
                val turipsResult: TuripResult<List<Turip>> = turipRepository.loadTurips()
                if (turipsResult is TuripResult.Failure) {
                    Napier.w("랜덤 여행 - 튜립 목록 조회 실패", turipsResult.cause)
                }

                when (val result: TuripResult<Trip> = contentRepository.loadTripInfo(contentId)) {
                    is TuripResult.Success -> showTuripDraft(contentId, result.value)
                    is TuripResult.Failure -> {
                        Napier.e("랜덤 여행 - 여행 장소 조회 실패", result.cause)
                        if (result.errorType.toUiError() == UiError.Global.TokenExpired) {
                            updateState { copy(turipDraftUiState = TuripDraftUiState.Hidden) }
                            navigateToLogin()
                        } else {
                            updateState { copy(turipDraftUiState = TuripDraftUiState.Error) }
                        }
                    }
                }
            }
    }

    private fun showTuripDraft(
        contentId: Long,
        trip: Trip,
    ) {
        val places: List<TuripDraftPlaceModel> =
            trip.contentPlaces
                .sortedWith(compareBy(ContentPlace::visitDay, ContentPlace::visitOrder))
                .map { it.toTuripDraftUiModel() }
                .distinctBy { it.placeId }

        if (places.isEmpty()) {
            Napier.d("랜덤 여행 - 담을 장소가 없어 영상으로 바로 이동")
            updateState { copy(turipDraftUiState = TuripDraftUiState.Hidden) }
            emitEffect(RandomTravelEffect.NavigateToTripDetail(contentId))
            return
        }

        val name: String = buildTuripName()
        updateState {
            copy(
                turipDraftUiState =
                    TuripDraftUiState.Ready(
                        name = name,
                        nameStatus = TuripNameStatusModel.of(name, existingTurips()),
                        places = places.toImmutableList(),
                    ),
            )
        }
    }

    /**
     * `제주 여행` 처럼 여행지에서 이름을 짓고, 이미 같은 이름이 있으면 `제주 여행 2` 로 번호를 붙인다.
     * 튜립명 최대 길이를 넘지 않도록 여행지 쪽을 잘라 낸다.
     */
    private fun buildTuripName(): String {
        val destinationName: String = currentState.destination?.name.orEmpty()
        val existingNames: Set<String> = existingTurips().map { it.name }.toSet()
        val baseName: String =
            "$destinationName$TURIP_NAME_SUFFIX".trim().take(TuripNameStatus.MAX_LENGTH)

        if (baseName !in existingNames) return baseName

        for (index in 2..MAX_TURIP_NAME_ATTEMPTS) {
            val suffix = " $index"
            val candidate: String =
                baseName.take(TuripNameStatus.MAX_LENGTH - suffix.length) + suffix
            if (candidate !in existingNames) return candidate
        }
        return baseName
    }

    private fun existingTurips(): List<TuripEditModel> =
        turipRepository.turips.value.map { turip: Turip ->
            TuripEditModel(id = turip.id, name = turip.name, count = turip.placeCount)
        }

    private fun changeTuripDraftName(name: String) {
        val draft: TuripDraftUiState.Ready = readyTuripDraft() ?: return
        updateState {
            copy(
                turipDraftUiState =
                    draft.copy(
                        name = name,
                        nameStatus = TuripNameStatusModel.of(name, existingTurips()),
                    ),
            )
        }
    }

    private fun toggleTuripDraftPlace(placeId: Long) {
        val draft: TuripDraftUiState.Ready = readyTuripDraft() ?: return
        if (draft.isSaving) return

        val places: ImmutableList<TuripDraftPlaceModel> =
            draft.places
                .map { place: TuripDraftPlaceModel ->
                    if (place.placeId == placeId) place.copy(isSelected = !place.isSelected) else place
                }.toImmutableList()
        updateState { copy(turipDraftUiState = draft.copy(places = places)) }
    }

    private fun toggleTuripDraftAllPlaces() {
        val draft: TuripDraftUiState.Ready = readyTuripDraft() ?: return
        if (draft.isSaving) return

        val isSelected: Boolean = !draft.isAllSelected
        val places: ImmutableList<TuripDraftPlaceModel> =
            draft.places
                .map { place: TuripDraftPlaceModel -> place.copy(isSelected = isSelected) }
                .toImmutableList()
        updateState { copy(turipDraftUiState = draft.copy(places = places)) }
    }

    private fun watchVideoAndReturn() {
        val contentId: Long = currentState.selectedContentId ?: return
        turipDraftJob?.cancel()
        emitEffect(RandomTravelEffect.NavigateToTripDetail(contentId))
    }

    private fun dismissTuripDraft() {
        if (readyTuripDraft()?.isSaving == true) return
        turipDraftJob?.cancel()
        updateState { copy(turipDraftUiState = TuripDraftUiState.Hidden) }
    }

    /**
     * 튜립을 만든 뒤 선택한 장소를 한 번에 담는다.
     *
     * 튜립 생성과 장소 담기는 별개 요청이라, 장소 담기가 실패해도 튜립은 이미 만들어져 있다.
     * 되돌리지 않고 몇 곳이 담겼는지를 그대로 알린다.
     */
    private fun confirmTuripDraft() {
        val draft: TuripDraftUiState.Ready = readyTuripDraft() ?: return
        if (!draft.canConfirm) return
        val contentId: Long = currentState.selectedContentId ?: return

        val turipName: String = draft.name.trim()
        val placeIds: List<Long> = draft.selectedPlaceIds

        turipDraftJob?.cancel()
        turipDraftJob =
            viewModelScope.launch {
                updateState { copy(turipDraftUiState = draft.copy(isSaving = true)) }

                val turip: Turip =
                    when (val result: TuripResult<Turip> = turipRepository.createTurip(turipName)) {
                        is TuripResult.Success -> result.value
                        is TuripResult.Failure -> {
                            Napier.e("랜덤 여행 - 튜립 생성 실패: $turipName", result.cause)
                            handleTuripCreateFailure(result, draft)
                            return@launch
                        }
                    }

                val savedCount: Int =
                    when (
                        val result: TuripResult<List<Long>> =
                            turipRepository.createTuripPlaces(turip.id, placeIds)
                    ) {
                        is TuripResult.Success -> result.value.size
                        is TuripResult.Failure -> {
                            Napier.e("랜덤 여행 - 장소 일괄 담기 실패: ${turip.id}", result.cause)
                            0
                        }
                    }

                Napier.d("랜덤 여행 - '$turipName' 튜립에 ${placeIds.size}곳 중 ${savedCount}곳 담음")
                updateState { copy(turipDraftUiState = TuripDraftUiState.Hidden) }
                emitEffect(
                    RandomTravelEffect.ShowTuripCreated(
                        turipName = turipName,
                        savedCount = savedCount,
                        requestedCount = placeIds.size,
                    ),
                )
                emitEffect(RandomTravelEffect.NavigateToTripDetail(contentId))
            }
    }

    private fun handleTuripCreateFailure(
        failure: TuripResult.Failure,
        draft: TuripDraftUiState.Ready,
    ) {
        when {
            failure.errorType == ErrorType.Turip.DuplicatedName ->
                updateState {
                    copy(
                        turipDraftUiState =
                            draft.copy(
                                isSaving = false,
                                nameStatus = TuripNameStatusModel.DUPLICATE_NAME,
                            ),
                    )
                }

            failure.errorType.toUiError() == UiError.Global.TokenExpired -> {
                updateState { copy(turipDraftUiState = TuripDraftUiState.Hidden) }
                navigateToLogin()
            }

            else -> {
                updateState { copy(turipDraftUiState = draft.copy(isSaving = false)) }
                emitEffect(RandomTravelEffect.ShowTuripCreateFailed)
            }
        }
    }

    private fun readyTuripDraft(): TuripDraftUiState.Ready? =
        currentState.turipDraftUiState as? TuripDraftUiState.Ready

    /**
     * 슬롯이 돌기 전에 발생한 에러. 돌다가 실패하는 그림을 만들지 않기 위해 전체 화면 에러로 처리한다.
     */
    private fun handlePreSpinError(failure: TuripResult.Failure) {
        when (failure.errorType.toUiError()) {
            UiError.Global.Network -> updateState { copy(errorUiState = ErrorUiState.Network) }
            UiError.Global.Server -> updateState { copy(errorUiState = ErrorUiState.Server) }
            UiError.Global.TokenExpired -> navigateToLogin()
            else -> updateState { copy(errorUiState = ErrorUiState.Unexpected) }
        }
    }

    /**
     * 티켓은 이미 발급됐으므로 유지하고 본문만 에러 + 재시도로 처리한다.
     */
    private fun handleBriefingError(failure: TuripResult.Failure) {
        val briefingError: ErrorUiState =
            when (failure.errorType.toUiError()) {
                UiError.Global.Network -> ErrorUiState.Network
                UiError.Global.TokenExpired -> {
                    navigateToLogin()
                    return
                }

                else -> ErrorUiState.Server
            }
        updateState {
            copy(
                isBriefingLoading = false,
                isBriefingFetched = true,
                briefingErrorUiState = briefingError,
            )
        }
    }

    private fun navigateToLogin() {
        viewModelScope.launch {
            sessionManager.switchToGuest()
            emitEffect(RandomTravelEffect.NavigateToLogin)
        }
    }

    companion object {
        private const val IS_DOMESTIC: Boolean = true
        private const val DOMESTIC_ETC_REGION_NAME: String = "국내 기타"
        private const val MAX_DRAW_ATTEMPTS: Int = 3
        private const val REEL_SIZE: Int = 24

        private const val BRIEFING_PAGE_SIZE: Int = 20
        private const val INITIAL_LAST_ID: Long = 0L
        private const val TURIP_NAME_SUFFIX: String = " 여행"
        private const val MAX_TURIP_NAME_ATTEMPTS: Int = 20
    }
}
