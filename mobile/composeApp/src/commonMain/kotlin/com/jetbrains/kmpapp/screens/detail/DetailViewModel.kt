package com.on.turip.screens.detail

import androidx.lifecycle.ViewModel
import com.on.turip.data.MuseumObject
import com.on.turip.data.MuseumRepository
import kotlinx.coroutines.flow.Flow

class DetailViewModel(private val museumRepository: MuseumRepository) : ViewModel() {
    fun getObject(objectId: Int): Flow<MuseumObject?> =
        museumRepository.getObjectById(objectId)
}
