package com.on.turip.ui.search.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.on.turip.ui.common.model.DayModel
import com.on.turip.ui.common.model.initDayModels

class SearchDetailViewModel : ViewModel() {
    private val _days: MutableLiveData<List<DayModel>> = MutableLiveData()
    val days: LiveData<List<DayModel>> = _days

    init {
        val loadDay: Int = 10 // TODO: 서버에서 받아온 여행일정 X일을 추출해서 추가
        _days.value = loadDay.initDayModels()
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
    }
}
