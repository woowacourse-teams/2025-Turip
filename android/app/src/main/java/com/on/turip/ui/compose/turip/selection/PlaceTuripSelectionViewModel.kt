package com.on.turip.ui.compose.turip.selection

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.common.AuthState
import com.on.turip.common.UserType
import com.on.turip.core.result.ErrorType
import com.on.turip.core.result.onFailure
import com.on.turip.core.result.onSuccess
import com.on.turip.domain.bookmark.TuripPlace
import com.on.turip.domain.bookmark.usecase.UpdateTuripPlaceUseCase
import com.on.turip.domain.turip.repository.TuripRepository
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.error.UiError
import com.on.turip.ui.common.error.toUiError
import com.on.turip.ui.compose.turip.selection.model.DeletePlaceSnapshot
import com.on.turip.ui.compose.turip.selection.model.TuripPlaceModel
import com.on.turip.ui.main.favorite.PlaceTuripSelectionFragment.Companion.PLACE_TURIP_SELECTION_ARGUMENTS_PLACE_ID
import com.on.turip.ui.main.favorite.PlaceTuripSelectionFragment.Companion.PLACE_TURIP_SELECTION_ARGUMENTS_PLACE_NAME
import com.on.turip.ui.main.favorite.model.TuripModel
import com.on.turip.ui.main.favorite.model.TuripShareModel
import com.on.turip.ui.main.favorite.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PlaceTuripSelectionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val turipRepository: TuripRepository,
    private val updateTuripPlaceUseCase: UpdateTuripPlaceUseCase,
) : ViewModel() {
    private val placeId: Long by lazy {
        checkNotNull(savedStateHandle[PLACE_TURIP_SELECTION_ARGUMENTS_PLACE_ID]) {
            Timber.e("장소에 대한 튜립 목록 바텀시트, place ID 값이 존재하지 않습니다.")
        }
    }

    private val placeName: String by lazy {
        checkNotNull(savedStateHandle[PLACE_TURIP_SELECTION_ARGUMENTS_PLACE_NAME]) {
            Timber.e("장소에 대한 튜립 목록 바텀시트, 장소명이 존재하지 않습니다.")
        }
    }

    // 튜립 목록의 버튼 활성화 여부를 위한 캐싱
    private var originTuripIds: Set<Long> = setOf()

    // 튜립 내 장소들 낙관적 UI를 위한 캐싱
    private var deletePlaceSnapshot: DeletePlaceSnapshot = DeletePlaceSnapshot.EMPTY
    private var reorderPlacesSnapshot: ImmutableList<TuripPlaceModel>? = null

    private val dragEndEvents = MutableSharedFlow<Unit>(replay = 0, extraBufferCapacity = 1)

    private val _uiState: MutableStateFlow<PlaceTuripSelectionUiState> =
        MutableStateFlow(PlaceTuripSelectionUiState.Idle)
    val uiState: StateFlow<PlaceTuripSelectionUiState> = _uiState.asStateFlow()

    private val _uiEffect: Channel<PlaceTuripSelectionUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<PlaceTuripSelectionUiEffect> = _uiEffect.receiveAsFlow()

    init {
        loadTuripsByPlace()
        registerDragEndEvents()
    }

    fun loadTuripsByPlace() {
        viewModelScope.launch {
            turipRepository
                .loadTuripsByPlaceId(placeId)
                .onSuccess {
                    val turips = it.map { turip -> turip.toUiModel() }.toImmutableList()

                    _uiState.update { state: PlaceTuripSelectionUiState ->
                        state.copy(
                            screenMode = PlaceTuripSelectionScreenMode.Turips,
                            placeName = placeName,
                            turips = turips,
                            selectedTuripPlaces = persistentListOf(),
                            isChanged = false,
                        )
                    }

                    originTuripIds =
                        turips
                            .filter { turip -> turip.isSelected }
                            .map { turip -> turip.id }
                            .toSet()
                    Timber.d("장소에 대한 튜립 목록 바텀시트, 튜립 목록 불러오기 성공")
                }.onFailure { errorType: ErrorType ->
                    sendErrorEffect(
                        errorType = errorType,
                        retryAction = PlaceTuripSelectionRetryAction.LoadTurips,
                    )
                    Timber.e("장소에 대한 튜립 목록 바텀시트, 튜립 목록 불러오기 실패")
                }
        }
    }

    @OptIn(FlowPreview::class)
    private fun registerDragEndEvents() {
        dragEndEvents
            .debounce(500L)
            .onEach { updateTuripPlacesOrder(uiState.value.selectedTuripPlaces) }
            .launchIn(viewModelScope)
    }

    fun updateTurip(turipModel: TuripModel) {
        val updateHasTuripPlace: Boolean = !turipModel.isSelected
        _uiState.update { state ->
            val updateTurips =
                state.turips
                    .map { turip ->
                        if (turip.id == turipModel.id) turip.copy(isSelected = updateHasTuripPlace) else turip
                    }.toImmutableList()

            state.copy(
                turips = updateTurips,
                isChanged = isTuripChanged(updateTurips),
            )
        }
    }

    fun updateTuripsByPlace() {
        viewModelScope.launch {
            val selectedTuripIds: List<Long> =
                uiState.value.turips
                    .filter { it.isSelected }
                    .map { it.id }

            turipRepository
                .updatePlaceTurips(placeId, selectedTuripIds)
                .onSuccess {
                    _uiEffect.send(
                        PlaceTuripSelectionUiEffect.UpdateTuripsByPlace(
                            placeId = placeId,
                            hasTurip = selectedTuripIds.isNotEmpty(),
                        ),
                    )
                    Timber.d("바텀시트, 선택한 장소에 대한 튜립들 현황 업데이트 성공")
                }.onFailure { errorType: ErrorType ->
                    sendErrorEffect(
                        errorType = errorType,
                        retryAction = PlaceTuripSelectionRetryAction.UpdateTuripsByPlace,
                    )
                    Timber.e("바텀시트, 선택한 장소에 대한 튜립들 현황 업데이트 실패")
                }
        }
    }

    private fun isTuripChanged(turips: ImmutableList<TuripModel>): Boolean {
        val currentTuripIds = turips.filter { it.isSelected }.map { it.id }.toSet()
        return originTuripIds != currentTuripIds
    }

    fun onTuripDetailBack() {
        _uiState.update {
            it.copy(
                screenMode = PlaceTuripSelectionScreenMode.Turips,
                selectedTuripPlaces = persistentListOf(),
            )
        }
    }

    fun loadPlacesInSelectTurip(
        turipId: Long,
        turipName: String,
    ) {
        viewModelScope.launch {
            turipRepository
                .loadTuripPlaces(turipId)
                .onSuccess { turipPlaces: List<TuripPlace> ->
                    _uiState.update { state: PlaceTuripSelectionUiState ->
                        state.copy(
                            screenMode =
                                PlaceTuripSelectionScreenMode.TuripDetail(
                                    turipId,
                                    turipName,
                                ),
                            selectedTuripPlaces =
                                turipPlaces.map { it.toUiModel() }.toImmutableList(),
                        )
                    }
                }.onFailure { errorType: ErrorType ->
                    sendErrorEffect(
                        errorType = errorType,
                        retryAction =
                            PlaceTuripSelectionRetryAction.LoadPlacesInTurip(
                                turipId = turipId,
                                turipName = turipName,
                            ),
                    )
                    Timber.e("튜립에 담긴 장소들을 불러오는 API 호출 실패 turipName = $turipName")
                }
        }
    }

    // 낙관적 UI / UI 반영만
    fun applyTuripPlaceDelete(place: TuripPlaceModel) {
        if (deletePlaceSnapshot.hasSnapshot()) return // 필요 없지만 혹시 모를 raceCondition 방지

        _uiState.update { state: PlaceTuripSelectionUiState ->
            deletePlaceSnapshot = DeletePlaceSnapshot(place, state.selectedTuripPlaces)
            val updatePlaces =
                state.selectedTuripPlaces
                    .filter { it.turipPlaceId != place.turipPlaceId }
                    .toImmutableList()
            state.copy(selectedTuripPlaces = updatePlaces)
        }
        viewModelScope.launch {
            _uiEffect.send(
                PlaceTuripSelectionUiEffect.ShowTuripPlaceRemoved(placeName = place.name),
            )
        }
    }

    // 낙관적 UI / UI 복구
    fun rollbackTuripPlaceDelete() {
        if (deletePlaceSnapshot.hasSnapshot()) {
            _uiState.update { it.copy(selectedTuripPlaces = deletePlaceSnapshot.originPlaces) }
            deletePlaceSnapshot = DeletePlaceSnapshot.EMPTY
        }
    }

    // 낙관적 UI / API 호출 & 조건에 따라 튜립 목록 화면 데이터 동기화
    fun commitTuripPlaceDelete() {
        if (!deletePlaceSnapshot.hasSnapshot()) {
            Timber.e("제거할 장소에 대한 정보가 없어요. deletePlaceSnapshot을 확인 해주세요 ")
            return
        }

        val deletePlace = deletePlaceSnapshot.deletePlace
        val screenMode = uiState.value.screenMode
        if (screenMode is PlaceTuripSelectionScreenMode.TuripDetail) {
            viewModelScope.launch {
                updateTuripPlaceUseCase(screenMode.turipId, deletePlace.placeId, false)
                    .onSuccess {
                        syncTuripForSelectedPlace(deletePlace, screenMode)
                        Timber.d("튜립 상세 바텀시트, 장소 업데이트 성공")
                    }.onFailure {
                        _uiEffect.send(
                            PlaceTuripSelectionUiEffect.ShowTuripPlaceRemoveFailed(deletePlace.name),
                        )
                        _uiState.update { it.copy(selectedTuripPlaces = deletePlaceSnapshot.originPlaces) }
                        Timber.e("튜립 상세 바텀시트, 장소 업데이트 실패 place = ${deletePlace.name}")
                    }

                // 장소 제거 스낵바에선 다시 시도 기능 제공 안하고 있어 성공 실패 여부와 없이 초기화
                deletePlaceSnapshot = DeletePlaceSnapshot.EMPTY
            }
        }
    }

    // '바텀 시트 진입할 때 선택한 장소' 와 폴더의 장소가 동일한 경우 데이터 동기화
    private fun syncTuripForSelectedPlace(
        deletePlace: TuripPlaceModel,
        screenMode: PlaceTuripSelectionScreenMode.TuripDetail,
    ) {
        if (placeId == deletePlace.placeId) {
            _uiState.update { state ->
                val syncTuripStatus =
                    state.turips
                        .map { if (it.id == screenMode.turipId) it.copy(isSelected = false) else it }
                        .toImmutableList()

                state.copy(turips = syncTuripStatus)
            }
            val updateCache =
                originTuripIds
                    .toMutableSet()
                    .apply { remove(screenMode.turipId) }
                    .toSet()
            originTuripIds = updateCache
        }
    }

    fun shareTurip() {
        val screenMode = uiState.value.screenMode
        if (screenMode is PlaceTuripSelectionScreenMode.TuripDetail) {
            when (AuthState.type) {
                UserType.MEMBER -> {
                    val turipShareModel =
                        TuripShareModel(
                            name = screenMode.turipName,
                            places =
                                uiState.value.selectedTuripPlaces
                                    .map { it.toUiModel() }
                                    .toImmutableList(),
                        )
                    viewModelScope.launch {
                        _uiEffect.send(
                            PlaceTuripSelectionUiEffect.ShareTurip(turipShareModel),
                        )
                    }
                }

                UserType.GUEST, UserType.NONE -> {
                    viewModelScope.launch {
                        _uiEffect.send(PlaceTuripSelectionUiEffect.TuripShareNotAllowed)
                    }
                }
            }
        }
    }

    // API 호출 실패 시 롤백을 위해 원본 상태 기록
    // 장소 제거 API가 반영되지 않은 상태라면 제거 전 원본 상태를 기록
    fun onDragStart() {
        reorderPlacesSnapshot =
            if (deletePlaceSnapshot.hasSnapshot()) deletePlaceSnapshot.originPlaces else uiState.value.selectedTuripPlaces
    }

    // 드래그 시 아이템 위치 변경
    fun onDragMove(
        from: Int,
        to: Int,
    ) {
        if (from == to) return
        _uiState.update { state ->
            val reOrderedPlaces =
                state.selectedTuripPlaces
                    .toMutableList()
                    .apply { add(to, removeAt(from)) }
                    .toImmutableList()
            state.copy(selectedTuripPlaces = reOrderedPlaces)
        }
    }

    // 드래그 후 데이터가 변경되었을 때만 tryEmit
    fun onDragEnd() {
        val current = uiState.value.selectedTuripPlaces
        if (reorderPlacesSnapshot == current) return

        dragEndEvents.tryEmit(Unit)
    }

    private fun updateTuripPlacesOrder(reorderedTuripPlaces: ImmutableList<TuripPlaceModel>) {
        val screenMode = uiState.value.screenMode
        if (screenMode is PlaceTuripSelectionScreenMode.TuripDetail) {
            viewModelScope.launch {
                turipRepository
                    .updateTuripPlacesOrder(
                        turipId = screenMode.turipId,
                        updatedOrder = reorderedTuripPlaces.map { it.turipPlaceId },
                    ).onSuccess {
                        _uiState.update { it.copy(selectedTuripPlaces = reorderedTuripPlaces) }
                        Timber.d("장소 순서 변경 API 성공")
                    }.onFailure {
                        if (reorderPlacesSnapshot != null) {
                            _uiState.update { it.copy(selectedTuripPlaces = reorderPlacesSnapshot!!) }
                        }
                        _uiEffect.send(
                            PlaceTuripSelectionUiEffect.ShowReorderPlaceFailed(
                                retryAction =
                                    PlaceTuripSelectionRetryAction
                                        .UpdateReorderedPlaces(reorderedTuripPlaces),
                            ),
                        )
                        Timber.e("장소 순서 변경 API 실패")
                    }
                reorderPlacesSnapshot = null
            }
        }
    }

    private suspend fun sendErrorEffect(
        errorType: ErrorType,
        retryAction: PlaceTuripSelectionRetryAction,
    ) {
        val uiError: UiError = errorType.toUiError()
        if (uiError is UiError.Global) {
            when (uiError) {
                UiError.Global.Network -> {
                    _uiEffect.send(
                        PlaceTuripSelectionUiEffect.ShowError(ErrorUiState.Network, retryAction),
                    )
                }

                UiError.Global.Server -> {
                    _uiEffect.send(
                        PlaceTuripSelectionUiEffect.ShowError(ErrorUiState.Server, retryAction),
                    )
                }

                UiError.Global.TokenExpired -> {
                    _uiEffect.send(PlaceTuripSelectionUiEffect.NavigateToLogin)
                }
            }
        }
    }

    fun handleErrorRetryRequest(action: PlaceTuripSelectionRetryAction) {
        when (action) {
            PlaceTuripSelectionRetryAction.LoadTurips -> {
                loadTuripsByPlace()
            }

            is PlaceTuripSelectionRetryAction.UpdateTurip -> {
                updateTurip(action.turipModel)
            }

            is PlaceTuripSelectionRetryAction.LoadPlacesInTurip -> {
                loadPlacesInSelectTurip(
                    turipId = action.turipId,
                    turipName = action.turipName,
                )
            }

            is PlaceTuripSelectionRetryAction.UpdateReorderedPlaces -> {
                updateTuripPlacesOrder(action.reorderedPlaces)
            }

            is PlaceTuripSelectionRetryAction.UpdateTuripsByPlace -> {
                updateTuripsByPlace()
            }
        }
    }
}
