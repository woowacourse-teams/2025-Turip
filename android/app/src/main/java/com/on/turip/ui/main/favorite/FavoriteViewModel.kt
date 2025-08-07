package com.on.turip.ui.main.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

class FavoriteViewModel : ViewModel() {
    companion object {
        fun provideFactory(): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    FavoriteViewModel()
                }
            }
    }
}
