package com.on.turip.ui.search.keywordresult

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

class SearchResultViewModel : ViewModel() {
    private val _searchingWord: MutableLiveData<String> = MutableLiveData()
    val searchingWord: LiveData<String> = _searchingWord

    fun updateSearchingWord(newWords: Editable?) {
        _searchingWord.value = newWords.toString()
    }

    companion object {
        fun provideFactory(): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    SearchResultViewModel()
                }
            }
    }
}
