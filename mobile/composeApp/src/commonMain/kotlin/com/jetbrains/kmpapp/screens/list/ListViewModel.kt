package com.on.turip.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.data.MuseumObject
import com.on.turip.data.MuseumRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ListViewModel(
    museumRepository: MuseumRepository,
) : ViewModel() {
    val objects: StateFlow<List<MuseumObject>> =
        museumRepository
            .getObjects()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
