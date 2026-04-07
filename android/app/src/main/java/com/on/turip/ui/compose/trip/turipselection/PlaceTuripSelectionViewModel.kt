package com.on.turip.ui.compose.trip.turipselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.core.result.ErrorType
import com.on.turip.core.result.onFailure
import com.on.turip.core.result.onSuccess
import com.on.turip.core.session.SessionState
import com.on.turip.domain.bookmark.TuripPlace
import com.on.turip.domain.session.SessionManager
import com.on.turip.domain.turip.TuripInvitationToken
import com.on.turip.domain.turip.repository.TuripRepository
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.error.UiError
import com.on.turip.ui.common.error.toUiError
import com.on.turip.ui.common.extensions.toUrl
import com.on.turip.ui.compose.trip.turipselection.model.DeletePlaceSnapshot
import com.on.turip.ui.compose.trip.turipselection.model.TuripPlaceModel
import com.on.turip.ui.compose.turipdetail.model.turip.TuripShareModel
import com.on.turip.ui.main.favorite.model.TuripModel
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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PlaceTuripSelectionViewModel @Inject constructor(
    private val turipRepository: TuripRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {
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

    private val sessionState: StateFlow<SessionState> = sessionManager.state

    init {
        registerDragEndEvents()
    }

    fun loadTuripsByPlace(
        placeId: Long,
        placeName: String,
    ) {
        viewModelScope.launch {
            turipRepository
                .loadTuripsByPlaceId(placeId)
                .onSuccess {
                    val turips = it.map { turip -> turip.toUiModel() }.toImmutableList()

                    _uiState.update { state: PlaceTuripSelectionUiState ->
                        state.copy(
                            selectionPlaceId = placeId,
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
                        retryAction = PlaceTuripSelectionRetryAction.LoadTurips(placeId, placeName),
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

            val placeId = uiState.value.selectionPlaceId

            turipRepository
                .updatePlaceTurips(placeId, selectedTuripIds)
                .onSuccess {
                    _uiEffect.send(
                        PlaceTuripSelectionUiEffect.UpdateTuripsByPlace(
                            placeId = placeId,
                            hasTurip = selectedTuripIds.isNotEmpty(),
                        ),
                    )
                    _uiEffect.send(PlaceTuripSelectionUiEffect.Dismiss)
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
        if (deletePlaceSnapshot.hasSnapshot()) commitTuripPlaceDelete()

        _uiState.update {
            it.copy(
                screenMode = PlaceTuripSelectionScreenMode.Turips,
                selectedTuripPlaces = persistentListOf(),
            )
        }
    }

    fun loadPlacesInSelectTurip(turipModel: TuripModel) {
        viewModelScope.launch {
            turipRepository
                .loadTuripPlaces(turipModel.id)
                .onSuccess { turipPlaces: List<TuripPlace> ->
                    _uiState.update { state: PlaceTuripSelectionUiState ->
                        state.copy(
                            screenMode = PlaceTuripSelectionScreenMode.TuripDetail(turipModel),
                            selectedTuripPlaces =
                                turipPlaces.map { it.toUiModel() }.toImmutableList(),
                        )
                    }
                }.onFailure { errorType: ErrorType ->
                    sendErrorEffect(
                        errorType = errorType,
                        retryAction = PlaceTuripSelectionRetryAction.LoadPlacesInTurip(turipModel = turipModel),
                    )
                    Timber.e("튜립에 담긴 장소들을 불러오는 API 호출 실패 turipName = ${turipModel.name}")
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
        viewModelScope.launch { commitTuripPlaceDeleteApi() }
    }

    private val commitMutex = Mutex()

    private suspend fun commitTuripPlaceDeleteApi() {
        commitMutex.withLock {
            if (!deletePlaceSnapshot.hasSnapshot()) {
                Timber.e("제거할 장소에 대한 정보가 없어요. deletePlaceSnapshot을 확인 해주세요 ")
                return
            }

            val deletePlace = deletePlaceSnapshot.deletePlace
            val screenMode = uiState.value.screenMode
            if (screenMode is PlaceTuripSelectionScreenMode.TuripDetail) {
                turipRepository
                    .deleteTuripPlace(screenMode.turipModel.id, deletePlace.placeId)
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
    private suspend fun syncTuripForSelectedPlace(
        deletePlace: TuripPlaceModel,
        screenMode: PlaceTuripSelectionScreenMode.TuripDetail,
    ) {
        if (uiState.value.selectionPlaceId == deletePlace.placeId) {
            _uiState.update { state ->
                val syncTuripStatus =
                    state.turips
                        .map { if (it.id == screenMode.turipModel.id) it.copy(isSelected = false) else it }
                        .toImmutableList()

                state.copy(turips = syncTuripStatus)
            }
            val updateCache =
                originTuripIds
                    .toMutableSet()
                    .apply { remove(screenMode.turipModel.id) }
                    .toSet()
            originTuripIds = updateCache

            val hasAnySelectedTurip = uiState.value.turips.any { it.isSelected }
            if (!hasAnySelectedTurip) {
                _uiEffect.send(PlaceTuripSelectionUiEffect.HasNoTuripsByPlace(uiState.value.selectionPlaceId))
            }
        }
    }

    fun shareTuripInvitationLink() {
        val screenMode = uiState.value.screenMode
        if (screenMode is PlaceTuripSelectionScreenMode.TuripDetail) {
            when (sessionState.value) {
                SessionState.Member -> {
                    viewModelScope.launch {
                        turipRepository
                            .createInvitationToken(screenMode.turipModel.id)
                            .onSuccess { token: TuripInvitationToken ->
                                _uiEffect.send(PlaceTuripSelectionUiEffect.ShareTuripInvitationLink(invitationLink = token.toUrl()))
                            }.onFailure { errorType ->
                                sendErrorEffect(
                                    errorType = errorType,
                                    retryAction = PlaceTuripSelectionRetryAction.ShareTuripInvitationLink,
                                )
                            }
                    }
                }

                SessionState.Guest, SessionState.Uninitialized -> {
                    viewModelScope.launch {
                        _uiEffect.send(PlaceTuripSelectionUiEffect.TuripShareNotAllowed)
                    }
                }
            }
        }
    }

    fun shareTuripByText() {
        val screenMode = uiState.value.screenMode
        if (screenMode is PlaceTuripSelectionScreenMode.TuripDetail) {
            when (sessionState.value) {
                SessionState.Member -> {
                    val turipShareModel =
                        TuripShareModel(
                            name = screenMode.turipModel.name,
                            places =
                                uiState.value.selectedTuripPlaces
                                    .map { it.toUiModel() }
                                    .toImmutableList(),
                        )
                    viewModelScope.launch {
                        _uiEffect.send(
                            PlaceTuripSelectionUiEffect.ShareTuripByText(turipShareModel),
                        )
                    }
                }

                SessionState.Guest, SessionState.Uninitialized -> {
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
                        turipId = screenMode.turipModel.id,
                        updatedOrder = reorderedTuripPlaces.map { it.turipPlaceId },
                    ).onSuccess {
                        _uiState.update { it.copy(selectedTuripPlaces = reorderedTuripPlaces) }
                        Timber.d("장소 순서 변경 API 성공")
                    }.onFailure { errorType ->
                        if (errorType is ErrorType.Auth) {
                            sessionManager.switchToGuest()
                            _uiEffect.send(PlaceTuripSelectionUiEffect.NavigateToLogin)
                            return@launch
                        }

                        if (reorderPlacesSnapshot != null) {
                            _uiState.update { it.copy(selectedTuripPlaces = reorderPlacesSnapshot!!) }
                        }
                        _uiEffect.send(
                            PlaceTuripSelectionUiEffect.ShowReorderPlaceFailed(
                                retryAction =
                                    PlaceTuripSelectionRetryAction.UpdateReorderedPlaces(reorderedTuripPlaces),
                            ),
                        )
                        Timber.e("장소 순서 변경 API 실패")
                    }
                reorderPlacesSnapshot = null
            }
        }
    }

    fun requestDismiss() {
        viewModelScope.launch {
            if (deletePlaceSnapshot.hasSnapshot()) commitTuripPlaceDeleteApi()
            if (uiState.value.screenMode is PlaceTuripSelectionScreenMode.TuripDetail) onTuripDetailBack()
            _uiEffect.send(PlaceTuripSelectionUiEffect.Dismiss)
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
                    sessionManager.switchToGuest()
                    _uiEffect.send(PlaceTuripSelectionUiEffect.NavigateToLogin)
                }
            }
        }
    }

    fun handleErrorRetryRequest(action: PlaceTuripSelectionRetryAction) {
        when (action) {
            is PlaceTuripSelectionRetryAction.LoadTurips -> {
                loadTuripsByPlace(action.placeId, action.placeName)
            }

            is PlaceTuripSelectionRetryAction.UpdateTurip -> {
                updateTurip(action.turipModel)
            }

            is PlaceTuripSelectionRetryAction.LoadPlacesInTurip -> {
                loadPlacesInSelectTurip(action.turipModel)
            }

            is PlaceTuripSelectionRetryAction.ShareTuripInvitationLink -> {
                shareTuripInvitationLink()
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
