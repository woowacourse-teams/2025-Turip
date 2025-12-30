package com.on.turip.ui.trip.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.data.common.ErrorType
import com.on.turip.data.common.ErrorUiEffect
import com.on.turip.data.common.ErrorUiState
import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.common.UiError
import com.on.turip.data.common.onFailure
import com.on.turip.data.common.onSuccess
import com.on.turip.data.common.toUiError
import com.on.turip.domain.content.Content
import com.on.turip.domain.content.repository.ContentRepository
import com.on.turip.domain.favorite.usecase.UpdateFavoriteUseCase
import com.on.turip.domain.trip.ContentPlace
import com.on.turip.domain.trip.Trip
import com.on.turip.domain.trip.repository.ContentPlaceRepository
import com.on.turip.ui.common.event.CommonUiEffect
import com.on.turip.ui.common.mapper.toUiModel
import com.on.turip.ui.trip.detail.TripDetailActivity.Companion.TRIP_DETAIL_CONTENT_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TripDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val contentRepository: ContentRepository,
    private val contentPlaceRepository: ContentPlaceRepository,
    private val updateFavoriteUseCase: UpdateFavoriteUseCase,
) : ViewModel() {
    private var placeCacheByDay: Map<Int, List<PlaceModel>> = emptyMap()

    // video load 여러번 호출되는 것을 방지
    var videoLoaded: Boolean = false
        private set

    private val _uiState: MutableStateFlow<TripDetailUiState> =
        MutableStateFlow(TripDetailUiState.IDLE)
    val uiState: StateFlow<TripDetailUiState> = _uiState.asStateFlow()

    private val _uiEffect: Channel<TripDetailUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<TripDetailUiEffect> = _uiEffect.receiveAsFlow()

    private val _commonUiEffect: Channel<CommonUiEffect> = Channel(Channel.BUFFERED)
    val commonUiEffect: Flow<CommonUiEffect> = _commonUiEffect.receiveAsFlow()

    private val _errorUiEffect: Channel<ErrorUiEffect> = Channel(Channel.BUFFERED)
    val errorUiEffect: Flow<ErrorUiEffect> = _errorUiEffect.receiveAsFlow()

    private val contentId: Long by lazy {
        checkNotNull(savedStateHandle[TRIP_DETAIL_CONTENT_KEY]) {
            Timber.e("컨텐츠 상세 화면 Content ID 값이 존재하지 않습니다.")
        }
    }

    init {
        loadTripDetails()
    }

    fun loadTripDetails() {
        if (uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorUiState = ErrorUiState.None) }

            val contentDeferred = async { contentRepository.loadContent(contentId) }
            val tripInfoDeferred = async { contentPlaceRepository.loadTripInfo(contentId) }

            val contentResult: TuripCustomResult<Content> = contentDeferred.await()
            val tripInfoResult: TuripCustomResult<Trip> = tripInfoDeferred.await()

            val failure: TuripCustomResult.Failure? =
                listOf(contentResult, tripInfoResult)
                    .filterIsInstance<TuripCustomResult.Failure>()
                    .firstOrNull()

            if (failure != null) {
                handleError(failure)
                return@launch
            }

            val content: Content = (contentResult as TuripCustomResult.Success).value
            val trip: Trip = (tripInfoResult as TuripCustomResult.Success).value

            setupCached(trip)

            _uiState.update { state: TripDetailUiState ->
                state.copy(
                    isLoading = false,
                    errorUiState = ErrorUiState.None,
                    days =
                        placeCacheByDay.keys.sorted().mapIndexed { index, day ->
                            DayModel(day = day, isSelected = index == DayModel.ALL_PLACE)
                        },
                    places = placeCacheByDay[DayModel.ALL_PLACE] ?: emptyList(),
                    tripDetailInfo =
                        TripDetailInfoModel(
                            creatorName = content.creator.channelName,
                            creatorThumbnail = content.creator.profileImage,
                            videoLink = content.videoData.url,
                            contentTitle = content.videoData.title,
                            uploadedDate = content.videoData.uploadedDate,
                            placeTotalCount = trip.tripPlaceCount,
                            duration = trip.tripDuration.toUiModel(),
                        ),
                    isFavorite = content.isFavorite,
                    isExpandTextToggleVisible = null,
                    isExpandTextToggleSelected = false,
                )
            }

            Timber.d("컨텐츠 상세 화면 모든 데이터 불러오기 성공")
        }
    }

    private fun setupCached(trip: Trip) {
        val placesByDay: MutableMap<Int, List<PlaceModel>> =
            trip.contentPlaces
                .sortedBy { it.visitDay }
                .groupBy(
                    keySelector = { contentPlace: ContentPlace -> contentPlace.visitDay },
                    valueTransform = { contentPlace: ContentPlace -> contentPlace.toUiModel() },
                ).toMutableMap()
        placesByDay[DayModel.ALL_PLACE] = placesByDay.toSortedMap().flatMap { it.value }

        placeCacheByDay = placesByDay
    }

    fun updateDay(updateDayModel: DayModel) {
        _uiState.update { state: TripDetailUiState ->
            val updatedDays: List<DayModel> =
                state.days.map { dayModel: DayModel -> dayModel.copy(isSelected = dayModel.day == updateDayModel.day) }
            state.copy(
                days = updatedDays,
                places = placeCacheByDay[updateDayModel.day].orEmpty(),
            )
        }
    }

    fun updateFavorite() {
        val updatedIsFavorite = !uiState.value.isFavorite
        viewModelScope.launch {
            updateFavoriteUseCase(updatedIsFavorite, contentId)
                .onSuccess {
                    Timber.d("컨텐츠 찜 API 통신 성공")
                    _uiState.update { it.copy(isFavorite = updatedIsFavorite) }
                    _uiEffect.send(TripDetailUiEffect.ShowFavoriteStatus(updatedIsFavorite))
                }.onFailure { errorType: ErrorType ->
                    _uiState.update { it.copy(isLoading = false, errorUiState = ErrorUiState.None) }
                    val uiError: UiError = errorType.toUiError()
                    if (uiError is UiError.Global) {
                        when (uiError) {
                            UiError.Global.Network -> {
                                _errorUiEffect.send(
                                    ErrorUiEffect.ShowSnackbar(
                                        errorUiState = ErrorUiState.Network,
                                        onRetryClick = { updateFavorite() },
                                    ),
                                )
                            }

                            UiError.Global.Server -> {
                                _errorUiEffect.send(ErrorUiEffect.ShowSnackbar(errorUiState = ErrorUiState.Server))
                            }

                            UiError.Global.TokenExpired -> {
                                _commonUiEffect.send(CommonUiEffect.NavigateToLogin)
                            }
                        }
                    }
                    Timber.d("컨텐츠 찜 API 통신 실패")
                }
        }
    }

    fun updateExpandTextToggle() {
        val updateExpandTextSelected: Boolean = !uiState.value.isExpandTextToggleSelected
        _uiState.update { it.copy(isExpandTextToggleSelected = updateExpandTextSelected) }
    }

    fun updateExpandTextToggleVisibility(
        lineCount: Int,
        ellipsisCount: Int,
    ) {
        _uiState.update { it.updateExpandTextToggleVisibility(lineCount, ellipsisCount) }
    }

    fun updateHasFavoriteFolderInPlace(
        hasFavoriteFolder: Boolean,
        placeId: Long,
    ) {
        val updatedCachePlaces =
            placeCacheByDay.mapValues { (_, places: List<PlaceModel>) ->
                if (places.any { it.id == placeId }) {
                    places.map { place: PlaceModel -> if (place.id == placeId) place.copy(isFavorite = hasFavoriteFolder) else place }
                } else {
                    places
                }
            }

        placeCacheByDay = updatedCachePlaces

        _uiState.update { state: TripDetailUiState ->
            state.copy(
                places =
                    uiState.value.places.map { place: PlaceModel ->
                        if (place.id == placeId) {
                            place.copy(isFavorite = hasFavoriteFolder)
                        } else {
                            place
                        }
                    },
            )
        }
    }

    fun updateVideoLoadStatus(isLoaded: Boolean) {
        videoLoaded = isLoaded
    }

    private suspend fun handleError(failure: TuripCustomResult.Failure) {
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
                    _commonUiEffect.send(CommonUiEffect.NavigateToLogin)
                }
            }
        }
    }
}
