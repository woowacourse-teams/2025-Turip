package com.on.turip.ui.compose.search.regionresult

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.core.result.TuripResult
import com.on.turip.domain.content.PagedContentsResult
import com.on.turip.domain.content.repository.ContentRepository
import com.on.turip.domain.content.video.VideoInformation
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.error.UiError
import com.on.turip.ui.common.error.toUiError
import com.on.turip.ui.common.mapper.toUiModel
import com.on.turip.ui.compose.search.model.VideoInformationModel
import com.on.turip.ui.search.regionresult.RegionResultActivity.Companion.REGION_RESULT_REGION_CATEGORY_NAME_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
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
    val regionCategoryName: String by lazy {
        checkNotNull(savedStateHandle[REGION_RESULT_REGION_CATEGORY_NAME_KEY]) {
            Timber.e("지역 검색 화면 지역 이름이 존재하지 않습니다.")
        }
    }

    private val _uiState: MutableStateFlow<RegionResultUiState> =
        MutableStateFlow(RegionResultUiState.Loading)
    val uiState: StateFlow<RegionResultUiState> = _uiState.asStateFlow()

    private val _uiEffect: Channel<RegionResultUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<RegionResultUiEffect> = _uiEffect.receiveAsFlow()

    init {
        loadContentsFromRegion()
    }

    fun loadContentsFromRegion() {
        viewModelScope.launch {
            _uiState.update { RegionResultUiState.Loading }

            val pagedContentsDeferred: Deferred<TuripResult<PagedContentsResult>> =
                async {
                    contentRepository.loadContentsByRegion(
                        regionCategoryName = regionCategoryName,
                        size = 100,
                        lastId = 0L,
                    )
                }
            val contentsSizeDeferred: Deferred<TuripResult<Int>> =
                async { contentRepository.loadContentsSizeByRegion(regionCategoryName) }

            val contentsResult: TuripResult<PagedContentsResult> =
                pagedContentsDeferred.await()
            val countResult: TuripResult<Int> = contentsSizeDeferred.await()

            val failure: TuripResult.Failure? =
                listOf(countResult, contentsResult)
                    .filterIsInstance<TuripResult.Failure>()
                    .firstOrNull()

            if (failure != null) {
                handleError(failure)
                return@launch
            }

            val count: Int = (countResult as TuripResult.Success).value
            val videosInformation: List<VideoInformationModel> =
                (contentsResult as TuripResult.Success).value.videos.map { videoInformation: VideoInformation ->
                    videoInformation.toUiModel()
                }

            if (count == 0) {
                _uiState.update { RegionResultUiState.Empty }
            } else {
                _uiState.update {
                    RegionResultUiState.Success(
                        videos = videosInformation.toImmutableList(),
                        totalCount = count,
                        region = regionCategoryName,
                    )
                }
            }

            Timber.d("지역에 대한 데이터 불러오기 성공, 지역 카테고리명 = $regionCategoryName")
        }
    }

    private suspend fun handleError(failure: TuripResult.Failure) {
        val uiError: UiError = failure.errorType.toUiError()
        if (uiError is UiError.Global) {
            when (uiError) {
                UiError.Global.Network -> _uiState.update { RegionResultUiState.Error(ErrorUiState.Network) }
                UiError.Global.Server -> _uiState.update { RegionResultUiState.Error(ErrorUiState.Server) }
                UiError.Global.TokenExpired -> _uiEffect.send(RegionResultUiEffect.NavigateToLogin)
            }
        }
    }
}
