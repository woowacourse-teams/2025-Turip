package com.on.turip.ui.travel.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TravelDetailViewModel : ViewModel() {
    private val _days: MutableLiveData<List<DayModel>> = MutableLiveData(emptyList())
    val days: LiveData<List<DayModel>> get() = _days

    private val _places: MutableLiveData<List<PlaceModel>> = MutableLiveData(emptyList())
    val places: LiveData<List<PlaceModel>> get() = _places

    private var placeCacheByDay: Map<DayModel, List<PlaceModel>>

    init {
        val loadDay: Int = 3 // TODO: 서버에서 받아온 여행일정 X일을 추출해서 추가
        _days.value = loadDay.initDayModels()
        placeCacheByDay = emptyMap() // TODO: 서버에서 받아온 데이터로 key:일차, value: 장소들로 map 자료구조로 캐싱
        _places.value = placeCacheByDay[DayModel(1)]
    }

    fun updateDay(day: DayModel) {
        val daysStatus: List<DayModel> = days.value ?: return // TODO: days.value null 일 때 로직 처리 필요
        val updateDaysStatus =
            daysStatus.map { dayModel ->
                if (dayModel.isSame(day)) {
                    dayModel.copy(isSelected = true)
                } else {
                    dayModel.copy(isSelected = false)
                }
            }
        _days.value = updateDaysStatus
        _places.value = placeCacheByDay[day]
    }
}
