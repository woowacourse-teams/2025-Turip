package com.on.turip.ui.main.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.on.turip.ui.main.favorite.model.HelpInformationModelType

class FavoriteHelpInformationViewModel : ViewModel() {
    private val _helpInformationItems = MutableLiveData<List<HelpInformationModelType>>()
    val helpInformationItems: LiveData<List<HelpInformationModelType>> get() = _helpInformationItems

    init {
        loadHelpInformationItems()
    }

    private fun loadHelpInformationItems() {
        _helpInformationItems.value =
            listOf(HelpInformationModelType.INQUIRY, HelpInformationModelType.PRIVACY_POLICY)
    }
}
