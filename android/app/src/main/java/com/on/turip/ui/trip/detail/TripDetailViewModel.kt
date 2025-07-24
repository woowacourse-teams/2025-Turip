package com.on.turip.ui.trip.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.on.turip.di.RepositoryModule
import com.on.turip.domain.videoinfo.contents.Content
import com.on.turip.domain.videoinfo.contents.VideoData
import com.on.turip.domain.videoinfo.contents.creator.Creator
import com.on.turip.domain.videoinfo.contents.creator.repository.CreatorRepository
import com.on.turip.domain.videoinfo.contents.repository.ContentRepository
import com.on.turip.domain.videoinfo.contents.video.trip.Trip
import com.on.turip.domain.videoinfo.contents.video.trip.TripCourse
import com.on.turip.domain.videoinfo.contents.video.trip.TripDuration
import com.on.turip.domain.videoinfo.contents.video.trip.repository.TripRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TripDetailViewModel(
    private val contentId: Long,
    private val creatorId: Long,
    private val contentRepository: ContentRepository,
    private val creatorRepository: CreatorRepository,
    private val tripRepository: TripRepository,
) : ViewModel() {
    private val _tripDetailState: MutableLiveData<TripDetailState> =
        MutableLiveData(TripDetailState())
    val tripDetailState: LiveData<TripDetailState> = _tripDetailState

    private var placeCacheByDay: Map<DayModel, List<PlaceModel>> = emptyMap()

    init {
        loadContent()
        loadTrip()
        setupTripDetails()
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
                    Log.e("TAG", "loadContent: $creator")
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
                        }
                }.onFailure {
                    Log.e("TAG", "loadContent ${it.message}")
                }
        }
    }

    private fun loadTrip() {
        viewModelScope.launch {
            val trip =
                async {
                    tripRepository.loadTripInfo(contentId)
                }

            trip
                .await()
                .onSuccess { trip: Trip ->
                    Log.e("TAG", "loadTrip: $trip ")
                    _tripDetailState.value =
                        tripDetailState.value?.copy(
                            trip = trip,
                        )
                    setupCache(trip)
                }.onFailure {
                    Log.d("TAG", "loadTrip: ${it.message}")
                }
        }
    }

    private fun setupTripDetails() {
        val loadDay = tripDetailState.value?.trip?.tripPlaceCount ?: 0
        _tripDetailState.value =
            tripDetailState.value?.copy(
                days = loadDay.initDayModels(),
            )
        _tripDetailState.value =
            tripDetailState.value?.copy(
                places = placeCacheByDay[DayModel(1)] ?: emptyList(),
            )
    }

    private fun setupCache(trip: Trip) {
        val dayModel: List<DayModel> = trip.tripDuration.days.initDayModels()

        placeCacheByDay =
            dayModel.associateWith { dayModel: DayModel ->
                val coursesForDay: List<TripCourse> =
                    trip.tripCourses.filter { it.visitDay == dayModel.day }
                coursesForDay.map { it: TripCourse ->
                    PlaceModel(
                        name = it.place.name,
                        category = it.place.category.toString(),
                        mapLink = it.place.url,
                    )
                }
            }
    }

    fun updateDay(day: DayModel) {
        val daysStatus: List<DayModel> =
            tripDetailState.value?.days ?: return // TODO: days.value null 일 때 로직 처리 필요
        val updateDaysStatus =
            daysStatus.map { dayModel ->
                if (dayModel.isSame(day)) {
                    dayModel.copy(isSelected = true)
                } else {
                    dayModel.copy(isSelected = false)
                }
            }
        _tripDetailState.value =
            tripDetailState.value?.copy(
                days = updateDaysStatus,
                places = placeCacheByDay[day] ?: emptyList(),
            )
    }

    companion object {
        fun provideFactory(
            contentId: Long,
            creatorId: Long,
            contentRepository: ContentRepository = RepositoryModule.contentRepository,
            creatorRepository: CreatorRepository = RepositoryModule.creatorRepository,
            travelRepository: TripRepository = RepositoryModule.tripRepository,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(TripDetailViewModel::class.java)) {
                        return TripDetailViewModel(
                            contentId,
                            creatorId,
                            contentRepository,
                            creatorRepository,
                            travelRepository,
                        ) as T
                    }
                    throw IllegalArgumentException()
                }
            }
    }

    data class TripDetailState(
        val content: Content =
            Content(
                id = 1,
                creator =
                    Creator(
                        id = 1,
                        channelName = "",
                        profileImage = "",
                    ),
                videoData =
                    VideoData(
                        title = "",
                        url = "",
                        uploadedDate = "",
                    ),
            ),
        val trip: Trip =
            Trip(
                tripDuration =
                    TripDuration(
                        nights = 1,
                        days = 1,
                    ),
                tripPlaceCount = 1,
                tripCourses = emptyList(),
            ),
        var days: List<DayModel> = emptyList<DayModel>(),
        val places: List<PlaceModel> = emptyList<PlaceModel>(),
    )
}
