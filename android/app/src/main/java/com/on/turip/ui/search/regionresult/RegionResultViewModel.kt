package com.on.turip.ui.search.regionresult

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.data.common.ErrorUiState
import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.common.UiError
import com.on.turip.data.common.toUiError
import com.on.turip.domain.content.PagedContentsResult
import com.on.turip.domain.content.repository.ContentRepository
import com.on.turip.domain.content.video.VideoInformation
import com.on.turip.ui.common.event.CommonUiEffect
import com.on.turip.ui.common.mapper.toUiModel
import com.on.turip.ui.search.model.VideoInformationModel
import com.on.turip.ui.search.regionresult.RegionResultActivity.Companion.REGION_RESULT_REGION_CATEGORY_NAME_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RegionResultViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val contentRepository: ContentRepository,
) : ViewModel() {
    private val regionCategoryName: String by lazy {
        checkNotNull(savedStateHandle[REGION_RESULT_REGION_CATEGORY_NAME_KEY]) {
            Timber.e("지역 검색 화면 지역 이름이 존재하지 않습니다.")
        }
    }

    private val _uiState: MutableStateFlow<RegionResultUiState> =
        MutableStateFlow(RegionResultUiState.Loading)
    val uiState: StateFlow<RegionResultUiState> = _uiState.asStateFlow()

    private val _commonUiEffect: Channel<CommonUiEffect> = Channel(Channel.BUFFERED)
    val commonUiEffect: Flow<CommonUiEffect> = _commonUiEffect.receiveAsFlow()

    init {
        loadContentsFromRegion()
    }

    fun reload() {
        loadContentsFromRegion()
    }

    private fun loadContentsFromRegion() {
        viewModelScope.launch {
            _uiState.update { RegionResultUiState.Loading }

            val pagedContentsDeferred: Deferred<TuripCustomResult<PagedContentsResult>> =
                async {
                    contentRepository.loadContentsByRegion(
                        regionCategoryName = regionCategoryName,
                        size = 100,
                        lastId = 0L,
                    )
                }
            val contentsSizeDeferred: Deferred<TuripCustomResult<Int>> =
                async { contentRepository.loadContentsSizeByRegion(regionCategoryName) }

            val contentsResult: TuripCustomResult<PagedContentsResult> =
                pagedContentsDeferred.await()
            val countResult: TuripCustomResult<Int> = contentsSizeDeferred.await()

            val failure: TuripCustomResult.Failure? =
                listOf(countResult, contentsResult)
                    .filterIsInstance<TuripCustomResult.Failure>()
                    .firstOrNull()

            if (failure != null) {
                handleError(failure)
                return@launch
            }

            val count: Int = (countResult as TuripCustomResult.Success).value
            val videosInformation: List<VideoInformationModel> =
                (contentsResult as TuripCustomResult.Success).value.videos.map { videoInformation: VideoInformation ->
                    videoInformation.toUiModel()
                }

            if (count == 0) {
                _uiState.update { RegionResultUiState.Empty }
            } else {
                _uiState.update {
                    RegionResultUiState.Success(
                        videos = videosInformation,
                        totalCount = count,
                        region = regionCategoryName,
                    )
                }
            }
        }
    }

    private suspend fun handleError(failure: TuripCustomResult.Failure) {
        when (val uiError: UiError = failure.errorType.toUiError()) {
            is UiError.Global -> handleGlobalError(uiError)
            is UiError.Feature -> Unit
        }
    }

    private suspend fun handleGlobalError(uiError: UiError.Global) {
        when (uiError) {
            UiError.Global.Network -> _uiState.update { RegionResultUiState.Error(ErrorUiState.Network) }
            UiError.Global.Server -> _uiState.update { RegionResultUiState.Error(ErrorUiState.Server) }
            UiError.Global.TokenExpired -> _commonUiEffect.send(CommonUiEffect.NavigateToLogin)
        }
    }
}
