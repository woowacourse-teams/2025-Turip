package com.on.turip.ui.main.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.on.turip.di.RepositoryModule
import com.on.turip.domain.favorite.repository.FavoritePlaceRepository
import com.on.turip.domain.favorite.usecase.UpdateFavoritePlaceUseCase

class FavoritePlaceFolderCatalogViewModel(
    private val favoritePlaceRepository: FavoritePlaceRepository,
    private val updateFavoritePlaceUseCase: UpdateFavoritePlaceUseCase,
) : ViewModel() {
    companion object {
        fun provideFactory(
            favoritePlaceRepository: FavoritePlaceRepository = RepositoryModule.favoritePlaceRepository,
            updateFavoritePlaceUseCase: UpdateFavoritePlaceUseCase =
                UpdateFavoritePlaceUseCase(
                    favoritePlaceRepository,
                ),
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    FavoritePlaceFolderCatalogViewModel(
                        favoritePlaceRepository,
                        updateFavoritePlaceUseCase,
                    )
                }
            }
    }
}
