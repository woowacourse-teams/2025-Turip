package com.on.turip.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.on.turip.di.RepositoryModule
import com.on.turip.domain.region.RegionCategory
import com.on.turip.domain.region.RegionRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel(
    private val regionRepository: RegionRepository,
) : ViewModel() {
    private val _regionCategories: MutableLiveData<List<RegionCategory>> = MutableLiveData()
    val regionCategories: LiveData<List<RegionCategory>> get() = _regionCategories

    private val _isSelectedDomestic: MutableLiveData<Boolean> = MutableLiveData()
    val isSelectedDomestic: LiveData<Boolean> get() = _isSelectedDomestic

    init {
        loadRegionCategories(isKorea = true)
    }

    fun loadRegionCategories(isKorea: Boolean) {
        viewModelScope.launch {
            regionRepository
                .loadRegionCategories(isKorea)
                .onSuccess { regionCategories: List<RegionCategory> ->
                    _regionCategories.value = regionCategories
                    _isSelectedDomestic.value = isKorea
                }.onFailure {
                    Timber.e("${it.message}")
                }
        }
    }

    companion object {
        fun provideFactory(regionRepository: RegionRepository = RepositoryModule.regionRepository): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    HomeViewModel(
                        regionRepository,
                    )
                }
            }
    }
}
