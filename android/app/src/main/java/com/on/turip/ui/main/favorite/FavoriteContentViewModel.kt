package com.on.turip.ui.main.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.data.common.onFailure
import com.on.turip.data.common.onSuccess
import com.on.turip.domain.ErrorEvent
import com.on.turip.domain.favorite.FavoriteContent
import com.on.turip.domain.favorite.repository.FavoriteRepository
import com.on.turip.domain.favorite.usecase.UpdateFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FavoriteContentViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val updateFavoriteUseCase: UpdateFavoriteUseCase,
) : ViewModel() {
    private val _favoriteContents: MutableLiveData<List<FavoriteContent>> =
        MutableLiveData(emptyList())
    val favoriteContents: LiveData<List<FavoriteContent>> get() = _favoriteContents

    private val _networkError: MutableLiveData<Boolean> = MutableLiveData(false)
    val networkError: LiveData<Boolean> get() = _networkError

    private val _serverError: MutableLiveData<Boolean> = MutableLiveData(false)
    val serverError: LiveData<Boolean> get() = _serverError

    init {
        loadFavoriteContents()
    }

    fun loadFavoriteContents() {
        viewModelScope.launch {
            favoriteRepository
                .loadFavoriteContents(10, 0L)
                .onSuccess {
                    Timber.d("찜 목록 데이터 조회 성공")
                    _favoriteContents.value = it.favoriteContents
                    _networkError.value = false
                    _serverError.value = false
                }.onFailure { errorEvent: ErrorEvent ->
                    checkError(errorEvent)
                    Timber.e("찜 목록 데이터 조회 에러 발생")
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
                        favoriteContents.value?.filter { it.content.id != contentId }

                    _networkError.value = false
                    _serverError.value = false
                }.onFailure { errorEvent: ErrorEvent ->
                    checkError(errorEvent)
                    Timber.d("찜 목록에서 찜 버튼 클릭 업데이트 실패 ")
                }
        }
    }

    private fun checkError(errorEvent: ErrorEvent) {
        when (errorEvent) {
            ErrorEvent.USER_NOT_HAVE_PERMISSION -> {
                _serverError.value = true
            }

            ErrorEvent.DUPLICATION_FOLDER -> throw IllegalArgumentException("발생할 수 없는 오류")
            ErrorEvent.UNEXPECTED_PROBLEM -> {
                _serverError.value = true
            }

            ErrorEvent.NETWORK_ERROR -> {
                _networkError.value = true
            }

            ErrorEvent.PARSER_ERROR -> {
                _serverError.value = true
            }
        }
    }
}
