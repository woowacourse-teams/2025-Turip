package com.on.turip.feature.trip.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.core.data.session.SessionManager
import com.on.turip.core.domain.repository.ContentRepository
import com.on.turip.core.domain.repository.TuripRepository
import com.on.turip.core.domain.usecase.UpdateBookmarkUseCase
import com.on.turip.core.model.content.Content
import com.on.turip.core.model.result.ErrorType
import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.model.result.onFailure
import com.on.turip.core.model.result.onSuccess
import com.on.turip.core.model.trip.ContentPlace
import com.on.turip.core.model.trip.Trip
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.core.ui.error.UiError
import com.on.turip.core.ui.error.toUiError
import com.on.turip.core.ui.model.namestatus.TuripNameStatusModel
import com.on.turip.feature.trip.impl.mapper.toUiModel
import com.on.turip.feature.trip.impl.model.PlaceModel
import com.on.turip.feature.trip.impl.model.SelectedPlaceModel
import com.on.turip.feature.trip.impl.model.TripDetailInfoModel
import io.github.aakira.napier.Napier
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TripDetailViewModel(
    private val contentRepository: ContentRepository,
    private val updateBookmarkUseCase: UpdateBookmarkUseCase,
    private val turipRepository: TuripRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {
    private val _uiState: MutableStateFlow<TripDetailUiState> =
        MutableStateFlow(TripDetailUiState.IDLE)
    val uiState: StateFlow<TripDetailUiState> = _uiState.asStateFlow()

    private val _uiEffect: Channel<TripDetailUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<TripDetailUiEffect> = _uiEffect.receiveAsFlow()

    private var contentId: Long = INVALID_ID
    private var videoPlaybackSecond: Int = 0
    private var confirmedBookmarked: Boolean = false
    private val bookmarkClickFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    // 화면 이탈로 viewModelScope가 취소돼도 북마크 API 요청은 끝까지 이어지도록 분리한 스코프
    private val bookmarkUpdateScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        handleBookmarkActions()
    }

    @OptIn(FlowPreview::class)
    private fun handleBookmarkActions() {
        bookmarkUpdateScope.launch {
            bookmarkClickFlow
                .debounce(BOOKMARK_DEBOUNCE_MS)
                .collect {
                    val targetBookmarked = uiState.value.isBookmarked
                    updateBookmarkUseCase(targetBookmarked, contentId)
                        .onSuccess {
                            Napier.d("북마크 업데이트 API 성공")
                            confirmedBookmarked = targetBookmarked
                            _uiEffect.send(TripDetailUiEffect.ShowBookmarkStatus(targetBookmarked))
                        }.onFailure { errorType: ErrorType ->
                            _uiState.update { it.copy(isBookmarked = confirmedBookmarked) }
                            sendErrorEffect(errorType, TripDetailRetryAction.UpdateBookmark)
                            Napier.e("북마크 업데이트 API 실패")
                        }
                }
        }
    }

    fun initContentId(contentId: Long) {
        if (this.contentId == contentId) return
        this.contentId = contentId
        videoPlaybackSecond = 0
        loadTripDetails()
    }

    fun updateVideoPlaybackSecond(second: Int) {
        videoPlaybackSecond = second.coerceAtLeast(0)
    }

    fun getCurrentVideoPlaybackSecond(): Int = videoPlaybackSecond

    fun loadTripDetails() {
        if (contentId == INVALID_ID) return
        if (uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val contentDeferred = async { contentRepository.loadContent(contentId) }
            val tripInfoDeferred = async { contentRepository.loadTripInfo(contentId) }

            val contentResult: TuripResult<Content> = contentDeferred.await()
            val tripInfoResult: TuripResult<Trip> = tripInfoDeferred.await()

            val failure: TuripResult.Failure? =
                listOf(contentResult, tripInfoResult)
                    .filterIsInstance<TuripResult.Failure>()
                    .firstOrNull()

            if (failure != null) {
                handleError(failure)
                return@launch
            }

            val content: Content = (contentResult as TuripResult.Success).value
            val trip: Trip = (tripInfoResult as TuripResult.Success).value

            val places =
                trip.contentPlaces
                    .sortedWith(compareBy(ContentPlace::visitDay, ContentPlace::visitOrder))
                    .map { contentPlace: ContentPlace -> contentPlace.toUiModel() }
                    .toImmutableList()

            _uiState.update { state: TripDetailUiState ->
                state.copy(
                    isLoading = false,
                    errorUiState = ErrorUiState.None,
                    places = places,
                    tripDetailInfo =
                        TripDetailInfoModel(
                            creatorName = content.creator.channelName,
                            creatorThumbnail = content.creator.profileImage,
                            city = content.city.name,
                            videoLink = content.videoData.url,
                            contentTitle = content.videoData.title,
                            uploadedDate = content.videoData.uploadedDate,
                            placeTotalCount = trip.tripPlaceCount,
                            duration = trip.tripDuration.toUiModel(),
                        ),
                    isBookmarked = content.isBookmarked.also { confirmedBookmarked = it },
                    selectedPlaceModel = null,
                )
            }

            Napier.d("컨텐츠 상세 화면 모든 데이터 불러오기 성공")
        }
    }

    fun updateBookmark() {
        _uiState.update { it.copy(isBookmarked = !it.isBookmarked) }
        bookmarkClickFlow.tryEmit(Unit)
    }

    fun updatePlaceTuripSelection(
        placeId: Long,
        hasTurip: Boolean,
    ) {
        viewModelScope.launch {
            val name = getPlaceName(placeId)
            _uiEffect.send(TripDetailUiEffect.ShowUpdatedTuripSelectionByPlace(name))
        }

        _uiState.update { state: TripDetailUiState ->
            val updatedPlaces =
                state.places
                    .map { place: PlaceModel ->
                        if (place.id == placeId) place.copy(isTuripPlace = hasTurip) else place
                    }.toImmutableList()
            state.copy(places = updatedPlaces)
        }
    }

    private fun getPlaceName(placeId: Long): String {
        val places = uiState.value.places
        return places.firstOrNull { place -> place.id == placeId }?.name ?: run {
            Napier.e("업데이트할 장소의 placeId를 찾을 수 없습니다. placeID = $placeId")
            ""
        }
    }

    fun selectPlace(
        placeId: Long,
        placeName: String,
    ) {
        _uiState.update { state ->
            state.copy(selectedPlaceModel = SelectedPlaceModel(placeId, placeName))
        }
    }

    fun clearSelectedPlace() {
        _uiState.update { it.copy(selectedPlaceModel = null) }
    }

    private suspend fun handleError(failure: TuripResult.Failure) {
        val uiError: UiError = failure.errorType.toUiError()
        if (uiError is UiError.Global) {
            when (uiError) {
                UiError.Global.Network -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorUiState = ErrorUiState.Network)
                    }
                }

                UiError.Global.Server -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorUiState = ErrorUiState.Server)
                    }
                }

                UiError.Global.TokenExpired -> {
                    _uiState.update { it.copy(isLoading = false) }
                    sessionManager.switchToGuest()
                    _uiEffect.send(TripDetailUiEffect.NavigateToLogin)
                }
            }
        }
    }

    fun showAddTuripBottomSheet() = _uiState.update { it.copy(showAddTuripBottomSheet = true) }

    fun dismissAddTuripBottomSheet() =
        _uiState.update {
            if (it.isCreatingTurip) {
                it.copy(showAddTuripBottomSheet = false)
            } else {
                it.copy(
                    showAddTuripBottomSheet = false,
                    addTuripInputName = "",
                    addTuripNameStatus = TuripNameStatusModel.EMPTY,
                )
            }
        }

    // 길이 제한은 NameEditorSheetContent 의 InputTransformation 이 담당한다.
    // 여기서 입력을 되돌리면 iOS 한글 IME 의 조합이 깨진다.
    fun updateAddTuripInputName(name: String) {
        val status = TuripNameStatusModel.of(name, persistentListOf())
        _uiState.update {
            it.copy(
                addTuripInputName = name,
                addTuripNameStatus = status,
            )
        }
    }

    fun addTurip() {
        val currentState = _uiState.value
        if (currentState.isCreatingTurip || !currentState.addTuripNameStatus.isConfirmEnabled) return
        _uiState.update { it.copy(isCreatingTurip = true) }
        val name = currentState.addTuripInputName
        viewModelScope.launch {
            turipRepository
                .createTurip(name)
                .onSuccess {
                    _uiState.update { it.copy(isCreatingTurip = false) }
                    _uiEffect.send(TripDetailUiEffect.TuripAdded(name))
                }.onFailure { errorType: ErrorType ->
                    if (errorType == ErrorType.Turip.DuplicatedName) {
                        _uiState.update {
                            it.copy(
                                isCreatingTurip = false,
                                addTuripNameStatus = TuripNameStatusModel.DUPLICATE_NAME,
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isCreatingTurip = false) }
                        sendErrorEffect(errorType, TripDetailRetryAction.AddTurip)
                    }
                }
        }
    }

    fun handleErrorRetryRequest(action: TripDetailRetryAction) {
        when (action) {
            TripDetailRetryAction.UpdateBookmark -> updateBookmark()
            TripDetailRetryAction.AddTurip -> addTurip()
        }
    }

    private suspend fun sendErrorEffect(
        errorType: ErrorType,
        retryAction: TripDetailRetryAction,
    ) {
        val uiError: UiError = errorType.toUiError()
        if (uiError is UiError.Global) {
            when (uiError) {
                UiError.Global.Network -> {
                    if (retryAction is TripDetailRetryAction.AddTurip) {
                        _uiEffect.send(TripDetailUiEffect.ShowAddedError(ErrorUiState.Network, retryAction))
                    } else {
                        _uiEffect.send(TripDetailUiEffect.ShowError(ErrorUiState.Network, retryAction))
                    }
                }

                UiError.Global.Server -> {
                    if (retryAction is TripDetailRetryAction.AddTurip) {
                        _uiEffect.send(TripDetailUiEffect.ShowAddedError(ErrorUiState.Server, retryAction))
                    } else {
                        _uiEffect.send(TripDetailUiEffect.ShowError(ErrorUiState.Server, retryAction))
                    }
                }

                UiError.Global.TokenExpired -> {
                    _uiEffect.send(TripDetailUiEffect.NavigateToLogin)
                }
            }
        }
    }

    private companion object {
        private const val INVALID_ID = -1L
        private const val BOOKMARK_DEBOUNCE_MS = 500L
    }
}
