package com.on.turip.ui.trip.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.on.turip.data.common.onFailure
import com.on.turip.data.common.onSuccess
import com.on.turip.domain.ErrorEvent
import com.on.turip.domain.content.Content
import com.on.turip.domain.content.repository.ContentRepository
import com.on.turip.domain.favorite.usecase.UpdateFavoriteUseCase
import com.on.turip.domain.trip.ContentPlace
import com.on.turip.domain.trip.Trip
import com.on.turip.domain.trip.repository.ContentPlaceRepository
import com.on.turip.ui.common.mapper.toUiModel
import com.on.turip.ui.common.mapper.toUiModelWithoutContentPlaces
import com.on.turip.ui.common.model.trip.TripModel
import com.on.turip.ui.trip.detail.TripDetailActivity.Companion.TRIP_DETAIL_CONTENT_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class TripDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val contentRepository: ContentRepository,
    private val contentPlaceRepository: ContentPlaceRepository,
    private val updateFavoriteUseCase: UpdateFavoriteUseCase,
) : ViewModel() {
    private val _content: MutableLiveData<Content> = MutableLiveData()
    val content: LiveData<Content> get() = _content

    private val _days: MutableLiveData<List<DayModel>> = MutableLiveData()
    val days: LiveData<List<DayModel>> get() = _days

    private val _places: MutableLiveData<List<PlaceModel>> = MutableLiveData()
    val places: LiveData<List<PlaceModel>> get() = _places

    private val _tripPlacesSummary: MutableLiveData<TripModel> = MutableLiveData()
    val tripPlacesSummary: LiveData<TripModel> get() = _tripPlacesSummary

    private val _videoUri: MutableLiveData<String> = MutableLiveData()
    val videoUri: LiveData<String> get() = _videoUri

    private val _isFavorite: MutableLiveData<Boolean> = MutableLiveData(false)
    val isFavorite: LiveData<Boolean> get() = _isFavorite

    private val _isExpandTextToggleVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    val isExpandTextToggleVisible: LiveData<Boolean> get() = _isExpandTextToggleVisible

    private val _isExpandTextToggleSelected: MutableLiveData<Boolean> = MutableLiveData(false)
    val isExpandTextToggleSelected: LiveData<Boolean> get() = _isExpandTextToggleSelected

    private val _bodyMaxLines: MutableLiveData<Int> =
        MutableLiveData(DEFAULT_CONTENT_TITLE_MAX_LINES)
    val bodyMaxLines: LiveData<Int> get() = _bodyMaxLines

    private var placeCacheByDay: Map<Int, List<PlaceModel>> = emptyMap()

    private val _networkError: MutableLiveData<Boolean> = MutableLiveData(false)
    val networkError: LiveData<Boolean> get() = _networkError

    private val _serverError: MutableLiveData<Boolean> = MutableLiveData(false)
    val serverError: LiveData<Boolean> get() = _serverError

    val tripDetailInfo: LiveData<TripDetailInfoModel> =
        content.switchMap { content ->
            tripPlacesSummary.map { tripModel ->
                TripDetailInfoModel(
                    content.videoData.uploadedDate,
                    tripModel.tripPlaceCount,
                    tripModel.tripDurationModel,
                )
            }
        }

    private val contentId: Long by lazy {
        checkNotNull(savedStateHandle[TRIP_DETAIL_CONTENT_KEY]) {
            Timber.e("컨텐츠 상세 화면 Content ID 값이 존재하지 않습니다.")
        }
    }

    init {
        loadVideoInformation()
        loadVideoPlaces()
    }

    fun reload() {
        loadVideoInformation()
        loadVideoPlaces()
    }

    private fun loadVideoInformation() {
        viewModelScope.launch {
            contentRepository
                .loadContent(contentId)
                .onSuccess { result: Content ->
                    _content.value = result
                    _videoUri.value = result.videoData.url
                    _isFavorite.value = result.isFavorite
                    clearErrors()
                }.onFailure { errorEvent: ErrorEvent ->
                    checkError(errorEvent)
                }
        }
    }

    private fun loadVideoPlaces() {
        if (placeCacheByDay.isNotEmpty()) return

        viewModelScope.launch {
            contentPlaceRepository
                .loadTripInfo(contentId)
                .onSuccess { trip: Trip ->
                    setupCached(trip)

                    _days.value =
                        placeCacheByDay.keys.sorted().mapIndexed { index, day ->
                            DayModel(day = day, isSelected = index == DayModel.ALL_PLACE)
                        }
                    _places.value = placeCacheByDay[DayModel.ALL_PLACE] ?: emptyList()
                    _tripPlacesSummary.value = trip.toUiModelWithoutContentPlaces()

                    Timber.d("여행 일정 불러오기 성공")
                    clearErrors()
                }.onFailure { errorEvent: ErrorEvent ->
                    checkError(errorEvent)
                }
        }
    }

    private fun setupCached(trip: Trip) {
        val placesByDay: MutableMap<Int, List<PlaceModel>> =
            trip.contentPlaces
                .groupBy(
                    keySelector = { contentPlace: ContentPlace -> contentPlace.visitDay },
                    valueTransform = { contentPlace: ContentPlace -> contentPlace.toUiModel() },
                ).toMutableMap()
        placesByDay[DayModel.ALL_PLACE] = placesByDay.flatMap { it.value }

        placeCacheByDay = placesByDay
    }

    fun updateDay(dayModel: DayModel) {
        _days.value = days.value?.map { it.copy(isSelected = it.day == dayModel.day) }
        _places.value = placeCacheByDay[dayModel.day].orEmpty()
    }

    fun updateFavorite() {
        _isFavorite.value = isFavorite.value?.not()
        handleFavoriteContent()
    }

    private fun handleFavoriteContent() {
        viewModelScope.launch {
            isFavorite.value?.let { isFavorite: Boolean ->
                updateFavoriteUseCase(
                    isFavorite,
                    contentId,
                ).onSuccess {
                    Timber.d("컨텐츠 찜 API 통신 성공")
                    clearErrors()
                }.onFailure { errorEvent: ErrorEvent ->
                    checkError(errorEvent)
                    Timber.d("컨텐츠 찜 API 통신 실패")
                }
            }
        }
    }

    fun updateExpandTextToggle() {
        val currentSelected: Boolean = _isExpandTextToggleSelected.value == true
        val newSelected: Boolean = !currentSelected

        _isExpandTextToggleSelected.value = newSelected
        _bodyMaxLines.value = if (newSelected) EXPAND_TEXT else DEFAULT_CONTENT_TITLE_MAX_LINES
    }

    fun updateExpandTextToggleVisibility(
        lineCount: Int,
        ellipsisCount: Int,
    ) {
        _isExpandTextToggleVisible.value =
            lineCount >= DEFAULT_CONTENT_TITLE_MAX_LINES &&
            ellipsisCount > 0
        _isExpandTextToggleSelected.value = false
        _bodyMaxLines.value = DEFAULT_CONTENT_TITLE_MAX_LINES
    }

    fun updateHasFavoriteFolderInPlace(
        hasFavoriteFolder: Boolean,
        placeId: Long,
    ) {
        val updatedCachePlaces =
            placeCacheByDay.mapValues { (_, value) ->
                value.map { place: PlaceModel ->
                    if (place.id == placeId) place.copy(isFavorite = hasFavoriteFolder) else place
                }
            }
        placeCacheByDay = updatedCachePlaces

        _places.value =
            places.value?.map { place: PlaceModel ->
                if (place.id == placeId) place.copy(isFavorite = hasFavoriteFolder) else place
            }
    }

    private fun checkError(errorEvent: ErrorEvent) {
        when (errorEvent) {
            ErrorEvent.USER_NOT_HAVE_PERMISSION -> _serverError.value = true
            ErrorEvent.UNEXPECTED_PROBLEM -> _serverError.value = true
            ErrorEvent.NETWORK_ERROR -> _networkError.value = true
            ErrorEvent.PARSER_ERROR -> _serverError.value = true
            ErrorEvent.DUPLICATION_FOLDER -> throw IllegalArgumentException("발생할 수 없는 오류")
        }
    }

    private fun clearErrors() {
        _serverError.value = false
        _networkError.value = false
    }

    companion object {
        private const val DEFAULT_CONTENT_TITLE_MAX_LINES = 2
        private const val EXPAND_TEXT = Int.MAX_VALUE
    }
}
