package com.on.turip.ui.search.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.on.turip.ui.common.model.DayModel
import com.on.turip.ui.common.model.PlaceModel
import com.on.turip.ui.common.model.getPlaceDummyWithDay
import com.on.turip.ui.common.model.initDayModels

class SearchDetailViewModel : ViewModel() {
    private val _days: MutableLiveData<List<DayModel>> = MutableLiveData(emptyList())
    val days: LiveData<List<DayModel>> = _days

    private val _places: MutableLiveData<List<PlaceModel>> = MutableLiveData(emptyList())
    val places: LiveData<List<PlaceModel>> = _places

    private var placeCacheByDay: Map<DayModel, List<PlaceModel>>

    init {
        val loadDay: Int = 3 // TODO: 서버에서 받아온 여행일정 X일을 추출해서 추가
        _days.value = loadDay.initDayModels()
        placeCacheByDay = getPlaceDummyWithDay()
        _places.value = placeCacheByDay[DayModel(1)]
    }

    fun selectDay(selectedDay: DayModel) {
        val currentList = days.value ?: return
        val newList =
            currentList.map { item ->
                if (item.day == selectedDay.day) {
                    item.copy(isSelected = true)
                } else {
                    item.copy(isSelected = false)
                }
            }
        _days.value = newList
        _places.value = placeCacheByDay[selectedDay]
    }
}
