package com.on.turip.ui.trip.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.on.turip.di.RepositoryModule
import com.on.turip.domain.content.Content
import com.on.turip.domain.content.repository.ContentRepository
import com.on.turip.domain.content.video.VideoData
import com.on.turip.domain.creator.Creator
import com.on.turip.domain.creator.repository.CreatorRepository
import com.on.turip.domain.favorite.usecase.UpdateFavoriteUseCase
import com.on.turip.domain.trip.Trip
import com.on.turip.domain.trip.repository.TripRepository
import com.on.turip.ui.common.mapper.toUiModel
import com.on.turip.ui.common.model.trip.TripDurationModel
import com.on.turip.ui.common.model.trip.TripModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import timber.log.Timber

class TripDetailViewModel(
    private val contentId: Long,
    private val creatorId: Long,
    private val contentRepository: ContentRepository,
    private val creatorRepository: CreatorRepository,
    private val tripRepository: TripRepository,
    private val updateFavoriteUseCase: UpdateFavoriteUseCase,
) : ViewModel() {
    private val _tripDetailState: MutableLiveData<TripDetailState> =
        MutableLiveData(TripDetailState())
    val tripDetailState: LiveData<TripDetailState> = _tripDetailState

    private val _videoUri: MutableLiveData<String> = MutableLiveData()
    val videoUri: LiveData<String> get() = _videoUri

    private val _isFavorite: MutableLiveData<Boolean> = MutableLiveData(false)
    val isFavorite: LiveData<Boolean> get() = _isFavorite

    private var placeCacheByDay: Map<Int, List<PlaceModel>> = emptyMap()

    init {
        loadContent()
        loadTrip()
        handleFavoriteWithDebounce()
    }

    private fun loadContent() {
        viewModelScope.launch {
            val creator: Deferred<Result<Creator>> =
                async {
                    creatorRepository.loadCreator(creatorId)
                }
            val videoData: Deferred<Result<VideoData>> =
                async {
                    contentRepository.loadContent(contentId)
                }

            creator
                .await()
                .onSuccess { creator: Creator ->
                    videoData
                        .await()
                        .onSuccess { videoData: VideoData ->
                            _tripDetailState.value =
                                tripDetailState.value?.copy(
                                    content =
                                        Content(
                                            id = contentId,
                                            creator = creator,
                                            videoData = videoData,
                                        ),
                                )
                            _videoUri.value = videoData.url
                            // _isFavorite.value = videoData.isFavorite
                        }
                }
        }
    }

    private fun loadTrip() {
        viewModelScope.launch {
            tripRepository
                .loadTripInfo(contentId)
                .onSuccess { trip: Trip ->
                    setupCached(trip)

                    _tripDetailState.value =
                        tripDetailState.value?.copy(
                            days =
                                placeCacheByDay.keys
                                    .sorted()
                                    .mapIndexed { index, day ->
                                        DayModel(day = day, isSelected = index == 0)
                                    },
                            places = placeCacheByDay[1] ?: emptyList(),
                            tripModel = trip.toUiModel(),
                        )
                }
        }
    }

    private fun setupCached(trip: Trip) {
        val dayModels = trip.tripDuration.days.initDayModels()

        placeCacheByDay =
            dayModels.associate { dayModel ->
                val day = dayModel.day
                val coursesForDay = trip.tripCourses.filter { it.visitDay == day }
                val placeModels =
                    coursesForDay.map { course ->
                        PlaceModel(
                            name = course.place.name,
                            category = course.place.category.joinToString(),
                            mapLink = course.place.url,
                        )
                    }
                day to placeModels
            }
    }

    fun updateDay(dayModel: DayModel) {
        tripDetailState.value?.let { state ->
            _tripDetailState.value =
                state.copy(
                    days = state.days.map { it.copy(isSelected = it.day == dayModel.day) },
                    places = placeCacheByDay[dayModel.day].orEmpty(),
                )
        }
    }

    fun updateFavorite() {
        _isFavorite.value = isFavorite.value?.not()
    }

    @OptIn(FlowPreview::class)
    private fun handleFavoriteWithDebounce() {
        viewModelScope.launch {
            _isFavorite
                .asFlow()
                .debounce(500L)
                .filterNotNull()
                .collectLatest { favoriteStatus: Boolean ->
                    updateFavoriteUseCase(favoriteStatus, contentId)
                        .onFailure {
                            Timber.e("${it.message}")
                        }
                }
        }
    }

    companion object {
        fun provideFactory(
            contentId: Long,
            creatorId: Long,
            contentRepository: ContentRepository = RepositoryModule.contentRepository,
            creatorRepository: CreatorRepository = RepositoryModule.creatorRepository,
            travelRepository: TripRepository = RepositoryModule.tripRepository,
            updateFavoriteUseCase: UpdateFavoriteUseCase = UpdateFavoriteUseCase(RepositoryModule.favoriteRepository),
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    TripDetailViewModel(
                        contentId,
                        creatorId,
                        contentRepository,
                        creatorRepository,
                        travelRepository,
                        updateFavoriteUseCase,
                    )
                }
            }
    }

    data class TripDetailState(
        val content: Content? = null,
        val days: List<DayModel> = emptyList<DayModel>(),
        val places: List<PlaceModel> = emptyList<PlaceModel>(),
        val tripModel: TripModel =
            TripModel(
                tripDurationModel = TripDurationModel(0, 0),
                tripPlaceCount = 0,
                tripCourses = emptyList(),
            ),
    )
}
