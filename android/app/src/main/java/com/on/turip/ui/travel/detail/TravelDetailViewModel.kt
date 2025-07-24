package com.on.turip.ui.travel.detail

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
import com.on.turip.domain.videoinfo.contents.video.trip.repository.TravelCourseRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TravelDetailViewModel(
    private val contentId: Long,
    private val creatorId: Long,
    private val contentRepository: ContentRepository,
    private val creatorRepository: CreatorRepository,
    private val travelRepository: TravelCourseRepository,
) : ViewModel() {
    private val _travelDetailState: MutableLiveData<TravelDetailState> = MutableLiveData()
    val travelDetailState: LiveData<TravelDetailState> = _travelDetailState

    private var placeCacheByDay: Map<DayModel, List<PlaceModel>> = emptyMap()

    init {
        setting()
        loadContent()
        loadTrip()
    }

    private fun setting() {
        val loadDay = travelDetailState.value?.trip?.tripPlaceCount ?: 0
        _travelDetailState.value =
            travelDetailState.value?.copy(
                days = loadDay.initDayModels(),
            )
        placeCacheByDay = emptyMap() // TODO: 서버에서 받아온 데이터로 key:일차, value: 장소들로 map 자료구조로 캐싱
        _travelDetailState.value =
            travelDetailState.value?.copy(
                places = placeCacheByDay[DayModel(1)] ?: emptyList(),
            )
    }

    private fun loadTrip() {
        viewModelScope.launch {
            val trip =
                async {
                    travelRepository.loadTripInfo(contentId)
                }

            trip.await().onSuccess { trip: Trip ->
                _travelDetailState.value =
                    travelDetailState.value?.copy(
                        trip = trip,
                    )
            }
        }
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

            creator.await().onSuccess { creator: Creator ->
                videoData.await().onSuccess { videoData: VideoData ->
                    _travelDetailState.value =
                        travelDetailState.value?.copy(
                            content =
                                Content(
                                    id = contentId,
                                    creator = creator,
                                    videoData = videoData,
                                ),
                        )
                }
            }
        }
    }

    fun updateDay(day: DayModel) {
        val daysStatus: List<DayModel> = travelDetailState.value?.days ?: emptyList()
        val updateDaysStatus =
            daysStatus.map { dayModel ->
                if (dayModel.isSame(day)) {
                    dayModel.copy(isSelected = true)
                } else {
                    dayModel.copy(isSelected = false)
                }
            }
        _travelDetailState.value =
            travelDetailState.value?.copy(
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
            travelRepository: TravelCourseRepository = RepositoryModule.travelCourseRepository,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(TravelDetailViewModel::class.java)) {
                        return TravelDetailViewModel(
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

    data class TravelDetailState(
        val content: Content,
        val trip: Trip,
        var days: List<DayModel> = emptyList<DayModel>(),
        val places: List<PlaceModel> = emptyList<PlaceModel>(),
    )
}
