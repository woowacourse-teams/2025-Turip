package com.on.turip.ui.main.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.on.turip.di.RepositoryModule
import com.on.turip.domain.favorite.FavoriteContent
import com.on.turip.domain.favorite.repository.FavoriteRepository
import com.on.turip.domain.favorite.usecase.UpdateFavoriteUseCase
import kotlinx.coroutines.launch
import timber.log.Timber

class FavoriteViewModel(
    private val favoriteRepository: FavoriteRepository,
    private val updateFavoriteUseCase: UpdateFavoriteUseCase,
) : ViewModel() {
    private val _favoriteContents: MutableLiveData<List<FavoriteContent>> = MutableLiveData()
    val favoriteContents: LiveData<List<FavoriteContent>> get() = _favoriteContents

    init {
        viewModelScope.launch {
            favoriteRepository
                .loadFavoriteContents(10, 0L)
                .onSuccess {
                    Timber.e("찜 목록 데이터 조회 성공")
                    _favoriteContents.value = it.favoriteContents
                }.onFailure {
                    Timber.e("찜 목록 데이터 조회 에러 발생")
                    Timber.e("${it.message}")
                }
        }
    }

    fun updateFavorite(
        contentId: Long,
        isFavorite: Boolean,
    ) {
        val updatedFavorite: Boolean = !isFavorite

        viewModelScope.launch {
            updateFavoriteUseCase(updatedFavorite, contentId)
                .onSuccess {
                    Timber.d("찜 목록 페이지, 찜 버튼 클릭(contentId=$contentId, updateFavorite = $updatedFavorite")
                    _favoriteContents.value =
                        _favoriteContents.value?.map {
                            if (it.content.id == contentId) {
                                val updateContent = it.content.copy(isFavorite = updatedFavorite)
                                it.copy(content = updateContent)
                            } else {
                                it
                            }
                        }
                }.onFailure {
                    Timber.d("찜 목록에서 찜 버튼 클릭 업데이트 실패 ")
                }
        }
    }

    companion object {
        fun provideFactory(
            favoriteRepository: FavoriteRepository = RepositoryModule.favoriteRepository,
            updateFavoriteUseCase: UpdateFavoriteUseCase = UpdateFavoriteUseCase(favoriteRepository),
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    FavoriteViewModel(
                        favoriteRepository = favoriteRepository,
                        updateFavoriteUseCase = updateFavoriteUseCase,
                    )
                }
            }
    }
}
