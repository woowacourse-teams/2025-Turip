package com.on.turip.ui.compose.trip

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.core.result.ErrorType
import com.on.turip.core.result.TuripResult
import com.on.turip.core.result.onFailure
import com.on.turip.core.result.onSuccess
import com.on.turip.domain.content.Content
import com.on.turip.domain.content.repository.ContentRepository
import com.on.turip.domain.favorite.usecase.UpdateBookmarkUseCase
import com.on.turip.domain.trip.ContentPlace
import com.on.turip.domain.trip.Trip
import com.on.turip.domain.trip.repository.ContentPlaceRepository
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.error.UiError
import com.on.turip.ui.common.error.toUiError
import com.on.turip.ui.common.mapper.toUiModel
import com.on.turip.ui.compose.trip.model.DayModel
import com.on.turip.ui.compose.trip.model.PlaceModel
import com.on.turip.ui.compose.trip.model.TripDetailInfoModel
import com.on.turip.ui.trip.TripDetailActivity.Companion.TRIP_DETAIL_CONTENT_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
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
    private val updateBookmarkUseCase: UpdateBookmarkUseCase,
) : ViewModel() {
    private var placeCacheByDay: Map<Int, ImmutableList<PlaceModel>> = emptyMap()

    private val _uiState: MutableStateFlow<TripDetailUiState> =
        MutableStateFlow(TripDetailUiState.IDLE)
    val uiState: StateFlow<TripDetailUiState> = _uiState.asStateFlow()

    private val _uiEffect: Channel<TripDetailUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<TripDetailUiEffect> = _uiEffect.receiveAsFlow()

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
            _uiState.update { it.copy(isLoading = true) }

            val contentDeferred = async { contentRepository.loadContent(contentId) }
            val tripInfoDeferred = async { contentPlaceRepository.loadTripInfo(contentId) }

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

            setupCached(trip)

            _uiState.update { state: TripDetailUiState ->
                state.copy(
                    isLoading = false,
                    errorUiState = ErrorUiState.None,
                    days =
                        placeCacheByDay.keys
                            .sorted()
                            .map { day: Int ->
                                DayModel(day = day, isSelected = day == DayModel.ALL_PLACE)
                            }.toImmutableList(),
                    places = placeCacheByDay[DayModel.ALL_PLACE] ?: persistentListOf(),
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
                    isBookmarked = content.isBookmarked,
                )
            }

            Timber.d("컨텐츠 상세 화면 모든 데이터 불러오기 성공")
        }
    }

    private fun setupCached(trip: Trip) {
        val placesByDay: MutableMap<Int, ImmutableList<PlaceModel>> =
            trip.contentPlaces
                .sortedBy { it.visitDay }
                .groupBy(
                    keySelector = { contentPlace: ContentPlace -> contentPlace.visitDay },
                    valueTransform = { contentPlace: ContentPlace -> contentPlace.toUiModel() },
                ).mapValues { it.value.toImmutableList() }
                .toMutableMap()

        placesByDay[DayModel.ALL_PLACE] =
            placesByDay
                .toSortedMap()
                .values
                .flatten()
                .toImmutableList()
        placeCacheByDay = placesByDay
    }

    fun updateDay(selectDay: Int) {
        _uiState.update { state: TripDetailUiState ->
            val updatedDays: ImmutableList<DayModel> =
                state.days
                    .map { dayModel: DayModel -> dayModel.copy(isSelected = dayModel.day == selectDay) }
                    .toImmutableList()
            state.copy(
                days = updatedDays,
                places = placeCacheByDay[selectDay] ?: persistentListOf(),
            )
        }
    }

    fun updateBookmark() {
        val updateBookmark = !uiState.value.isBookmarked
        viewModelScope.launch {
            updateBookmarkUseCase(updateBookmark, contentId)
                .onSuccess {
                    Timber.d("북마크 업데이트 API 성공")
                    _uiState.update { it.copy(isBookmarked = updateBookmark) }
                    _uiEffect.send(TripDetailUiEffect.ShowBookmarkStatus(updateBookmark))
                }.onFailure { errorType: ErrorType ->
                    _uiState.update { it.copy(isLoading = false) }
                    val uiError: UiError = errorType.toUiError()
                    if (uiError is UiError.Global) {
                        when (uiError) {
                            UiError.Global.Network -> {
                                _uiEffect.send(
                                    TripDetailUiEffect.ShowError(
                                        ErrorUiState.Network,
                                        TripDetailRetryAction.UpdateBookmark,
                                    ),
                                )
                            }

                            UiError.Global.Server -> {
                                _uiEffect.send(
                                    TripDetailUiEffect.ShowError(
                                        ErrorUiState.Server,
                                        TripDetailRetryAction.UpdateBookmark,
                                    ),
                                )
                            }

                            UiError.Global.TokenExpired -> {
                                _uiEffect.send(TripDetailUiEffect.NavigateToLogin)
                            }
                        }
                    }
                    Timber.d("북마크 업데이트 API 실패")
                }
        }
    }

    fun updateHasFavoriteFolderInPlace(
        hasFavoriteFolder: Boolean,
        placeId: Long,
    ) {
        placeCacheByDay =
            placeCacheByDay.mapValues { (_, places: ImmutableList<PlaceModel>) ->
                if (places.any { it.id == placeId }) {
                    places
                        .map { place: PlaceModel -> if (place.id == placeId) place.copy(isTuripPlace = hasFavoriteFolder) else place }
                        .toImmutableList()
                } else {
                    places
                }
            }

        _uiState.update { state: TripDetailUiState ->
            val currentSelectedDay = state.days.find { it.isSelected }?.day ?: DayModel.ALL_PLACE
            state.copy(places = placeCacheByDay[currentSelectedDay] ?: persistentListOf())
        }
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
                    _uiEffect.send(TripDetailUiEffect.NavigateToLogin)
                }
            }
        }
    }

    fun handleErrorRetryRequest(action: TripDetailRetryAction) {
        when (action) {
            TripDetailRetryAction.UpdateBookmark -> updateBookmark()
        }
    }
}
