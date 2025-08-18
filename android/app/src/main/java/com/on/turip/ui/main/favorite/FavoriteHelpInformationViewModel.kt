package com.on.turip.ui.main.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.on.turip.di.RepositoryModule
import com.on.turip.domain.userstorage.TuripDeviceIdentifier
import com.on.turip.domain.userstorage.repository.UserStorageRepository
import com.on.turip.ui.main.favorite.model.HelpInformationModelType
import kotlinx.coroutines.launch
import timber.log.Timber

class FavoriteHelpInformationViewModel(
    private val userStorageRepository: UserStorageRepository,
) : ViewModel() {
    private val _helpInformationItems: MutableLiveData<List<HelpInformationModelType>> =
        MutableLiveData<List<HelpInformationModelType>>()
    val helpInformationItems: LiveData<List<HelpInformationModelType>> get() = _helpInformationItems

    private val _userId: MutableLiveData<TuripDeviceIdentifier> = MutableLiveData()
    val userId: LiveData<TuripDeviceIdentifier> get() = _userId

    init {
        loadId()
        loadHelpInformationItems()
    }

    private fun loadId() {
        viewModelScope.launch {
            userStorageRepository
                .loadId()
                .onSuccess { result ->
                    _userId.value = result
                }.onFailure {
                    Timber.e("${it.message}")
                }
        }
    }

    private fun loadHelpInformationItems() {
        _helpInformationItems.value =
            listOf(HelpInformationModelType.INQUIRY, HelpInformationModelType.PRIVACY_POLICY)
    }

    companion object {
        fun provideFactory(
            userStorageRepository: UserStorageRepository = RepositoryModule.userStorageRepository,
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    FavoriteHelpInformationViewModel(userStorageRepository)
                }
            }
    }
}
