package com.on.turip.ui.compose.turipdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.core.result.ErrorType
import com.on.turip.core.result.onFailure
import com.on.turip.core.result.onSuccess
import com.on.turip.data.result.TuripStreamResult
import com.on.turip.domain.bookmark.TuripPlace
import com.on.turip.domain.session.SessionState
import com.on.turip.domain.session.SessionStore
import com.on.turip.domain.turip.DeleteTuripUseCase
import com.on.turip.domain.turip.ObserveTuripStreamUseCase
import com.on.turip.domain.turip.Turip
import com.on.turip.domain.turip.TuripInvitationToken
import com.on.turip.domain.turip.TuripStreamEvent
import com.on.turip.domain.turip.repository.TuripRepository
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.error.UiError
import com.on.turip.ui.common.error.toUiError
import com.on.turip.ui.common.extensions.toUrl
import com.on.turip.ui.common.model.namestatus.TuripNameStatusModel
import com.on.turip.ui.compose.trip.turipselection.model.TuripPlaceModel
import com.on.turip.ui.compose.turip.mapper.toUiMyTuripModel
import com.on.turip.ui.compose.turip.model.TuripTypeModel
import com.on.turip.ui.compose.turipdetail.model.DeleteTuripPlaceSnapshot
import com.on.turip.ui.compose.turipdetail.model.RefreshScope
import com.on.turip.ui.compose.turipdetail.model.turip.PlaceLatLngUiModel
import com.on.turip.ui.compose.turipdetail.model.turip.TuripShareModel
import com.on.turip.ui.folder.model.TuripEditModel
import com.on.turip.ui.main.favorite.toLatLng
import com.on.turip.ui.main.favorite.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.BufferOverflow
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
class TuripDetailViewModel @Inject constructor(
    private val turipRepository: TuripRepository,
    private val deleteTuripUseCase: DeleteTuripUseCase,
    private val observeTuripStreamUseCase: ObserveTuripStreamUseCase,
    sessionStore: SessionStore,
) : ViewModel() {
    private val _uiState: MutableStateFlow<TuripDetailUiState> =
        MutableStateFlow(TuripDetailUiState.Idle)
    val uiState: StateFlow<TuripDetailUiState> = _uiState.asStateFlow()

    private val _uiEffect: Channel<TuripDetailUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<TuripDetailUiEffect> = _uiEffect.receiveAsFlow()

    private val sessionState: StateFlow<SessionState> = sessionStore.state

    private var deleteTuripPlaceSnapshot: DeleteTuripPlaceSnapshot = DeleteTuripPlaceSnapshot.EMPTY
    private var reorderPlacesSnapshot: ImmutableList<TuripPlaceModel>? = null
    private var selectedTuripId: Long = INVALID_ID

    private val dragEndEvents = MutableSharedFlow<Unit>(replay = 0, extraBufferCapacity = 1)
    private val refreshTrigger =
        MutableSharedFlow<RefreshScope>(
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )

    private val deletePlaceQueue = ArrayDeque<TuripPlaceModel>()
    private val committedPlaceIds = mutableSetOf<Long>()
    private var originBeforeDeleteSession: Pair<ImmutableList<TuripPlaceModel>, ImmutableList<PlaceLatLngUiModel>>? =
        null

    private val commitMutex = Mutex()
    private val flushScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        registerDragEndEvents()
        registerStreamRefresh()
    }

    fun initIfNeeded(turipId: Long) {
        if (turipId == INVALID_ID) return
        if (selectedTuripId == turipId && uiState.value.selectedTurip.id == turipId) return

        selectedTuripId = turipId
        loadTuripData(turipId)
    }

    private fun loadTuripData(turipId: Long) {
        viewModelScope.launch {
            loadSelectedTurip(turipId)
            if (uiState.value.selectedTurip.type == TuripTypeModel.TOGETHER) {
                observeTuripStream(turipId)
            }
        }
        loadPlaces(turipId)
    }

    fun loadSelectedTuripAsync(selectedTuripId: Long) {
        viewModelScope.launch {
            loadSelectedTurip(selectedTuripId)
        }
    }

    private suspend fun loadSelectedTurip(selectedTuripId: Long) {
        turipRepository.loadTurip(selectedTuripId).onSuccess { turip: Turip ->
            _uiState.update { state: TuripDetailUiState ->
                state.copy(
                    errorUiState = ErrorUiState.None,
                    selectedTurip = turip.toUiMyTuripModel(),
                )
            }
        }
    }

    fun loadPlaces(selectedTuripId: Long) {
        viewModelScope.launch {
            turipRepository
                .loadTuripPlaces(selectedTuripId)
                .onSuccess { result: List<TuripPlace> ->
                    _uiState.update { state: TuripDetailUiState ->
                        state.copy(
                            isLoading = false,
                            errorUiState = ErrorUiState.None,
                            places =
                                result
                                    .map { turipPlace: TuripPlace -> turipPlace.toUiModel() }
                                    .toImmutableList(),
                            placesLatLng =
                                result
                                    .map { it.toLatLng() }
                                    .toImmutableList(),
                        )
                    }
                    clearSnapshots()
                }.onFailure { errorType: ErrorType ->
                    when (val uiError: UiError = errorType.toUiError()) {
                        is UiError.Global -> handleGlobalError(uiError)
                        is UiError.Feature -> Unit
                    }
                    Timber.e("튜립 장소 목록 조회 API 호출 실패")
                }
        }
    }

    private fun loadMembers() {
        viewModelScope.launch {
            turipRepository.loadTuripMembers(selectedTuripId).onSuccess { members: List<String> ->
                _uiState.update { it.copy(members = members.toImmutableList()) }
            }
        }
    }

    private fun observeTuripStream(turipId: Long) {
        viewModelScope.launch {
            observeTuripStreamUseCase(turipId)
                .collect { result ->
                    when (result) {
                        is TuripStreamResult.Event -> {
                            handleTuripStreamEvent(result.event)
                        }

                        is TuripStreamResult.Reconnecting -> {
                            Timber.w("SSE 재연결 중. turipId=$turipId, retryCount=${result.retryCount}")
                            if (result.retryCount >= UNSTABLE_NETWORK_RETRY_THRESHOLD) {
                                _uiEffect.send(TuripDetailUiEffect.ShowNetworkUnstable)
                            }
                        }

                        TuripStreamResult.Fatal.TokenExpired -> {
                            _uiEffect.send(TuripDetailUiEffect.NavigateToLogin)
                        }

                        TuripStreamResult.Fatal.Forbidden -> {
                            Timber.w("SSE 권한 없음, 스트림 중단. turipId=$turipId")
                        }

                        is TuripStreamResult.Fatal.ConnectionLost -> {
                            Timber.e("SSE 최대 재시도 초과, 스트림 종료. turipId=$turipId")
                            sendErrorEffect(
                                errorType = result.errorType,
                                retryAction = TuripPlaceRetryAction.StreamConnectionLost,
                            )
                        }
                    }
                }
        }
    }

    private fun handleTuripStreamEvent(event: TuripStreamEvent) {
        when (event) {
            is TuripStreamEvent.Connect -> {
                Timber.d("튜립 SSE 연결 성공: turipId=%s, eventId=%s", event.turipId, event.id)
                requestRefresh(turip = true, places = true)
                loadMembers()
            }

            is TuripStreamEvent.FolderUpdate -> {
                Timber.d(
                    "튜립 SSE 폴더 업데이트 수신: turipId=%s, action=%s, eventId=%s",
                    event.turipId,
                    event.action,
                    event.id,
                )
                when (event.action) {
                    TuripStreamEvent.FolderAction.FOLDER_NAME_CHANGED,
                    TuripStreamEvent.FolderAction.FOLDER_DELETED,
                    -> {
                        requestRefresh(turip = true, places = false)
                    }

                    TuripStreamEvent.FolderAction.PLACE_REORDERED,
                    TuripStreamEvent.FolderAction.PLACE_ADDED,
                    TuripStreamEvent.FolderAction.PLACE_DELETED,
                    TuripStreamEvent.FolderAction.FOLDER_PLACE_CHANGED,
                    -> {
                        requestRefresh(turip = true, places = true)
                    }

                    TuripStreamEvent.FolderAction.UNKNOWN -> {
                        requestRefresh(turip = true, places = true)
                    }
                }
            }

            is TuripStreamEvent.MemberUpdate -> {
                Timber.d(
                    "튜립 SSE 멤버 업데이트 수신: turipId=%s, action=%s, memberCount=%s, eventId=%s",
                    event.turipId,
                    event.action,
                    event.memberCount,
                    event.id,
                )
                requestRefresh(turip = true, places = false)
                loadMembers()
            }

            is TuripStreamEvent.Heartbeat -> {
                Timber.v("튜립 SSE 하트비트 수신: eventId=%s", event.id)
            }
        }
    }

    private fun requestRefresh(
        turip: Boolean,
        places: Boolean,
    ) {
        refreshTrigger.tryEmit(RefreshScope(turip = turip, places = places))
    }

    @OptIn(FlowPreview::class)
    private fun registerStreamRefresh() {
        refreshTrigger
            .debounce(300L)
            .onEach { scope: RefreshScope ->
                if (scope.turip) loadSelectedTurip(selectedTuripId)
                if (scope.places) loadPlaces(selectedTuripId)
            }.launchIn(viewModelScope)
    }

    fun applyTuripPlaceDelete(placeId: Long) {
        val targetPlace =
            uiState.value.places.find { it.placeId == placeId }
                ?: run {
                    Timber.e("삭제할 장소를 찾을 수 없어요. placeId = $placeId")
                    return
                }

        if (deletePlaceQueue.isEmpty()) {
            originBeforeDeleteSession = uiState.value.places to uiState.value.placesLatLng
        }

        deletePlaceQueue.addLast(targetPlace)

        _uiState.update { state: TuripDetailUiState ->
            state.copy(
                places =
                    state.places
                        .filter { it.turipPlaceId != targetPlace.turipPlaceId }
                        .toImmutableList(),
                placesLatLng =
                    state.placesLatLng
                        .filter { it.placeId != targetPlace.placeId }
                        .toImmutableList(),
            )
        }

        viewModelScope.launch {
            _uiEffect.send(TuripDetailUiEffect.ShowTuripDetailRemoved(targetPlace.name))
        }
    }

    fun commitTuripPlaceDelete() {
        viewModelScope.launch {
            commitMutex.withLock {
                val deletePlace = deletePlaceQueue.removeFirstOrNull() ?: return@launch

                turipRepository
                    .deleteTuripPlace(uiState.value.selectedTurip.id, deletePlace.placeId)
                    .onSuccess {
                        committedPlaceIds.add(deletePlace.placeId)
                        if (deletePlaceQueue.isEmpty()) {
                            clearDeleteSession()
                        }
                    }.onFailure {
                        _uiEffect.send(
                            TuripDetailUiEffect.ShowTuripDetailRemoveFailed(deletePlace.name),
                        )
                        rollbackTuripPlaceDelete()
                    }
            }
        }
    }

    fun rollbackTuripPlaceDelete() {
        val (originPlaces, originPlacesLatLng) = originBeforeDeleteSession ?: return

        val rolledBackPlaces =
            originPlaces
                .filter { it.placeId !in committedPlaceIds }
                .toImmutableList()
        val rolledBackLatLng =
            originPlacesLatLng
                .filter { it.placeId !in committedPlaceIds }
                .toImmutableList()

        _uiState.update { state ->
            state.copy(
                places = rolledBackPlaces,
                placesLatLng = rolledBackLatLng,
            )
        }
        clearDeleteSession()
    }

    private fun clearDeleteSession() {
        originBeforeDeleteSession = null
        committedPlaceIds.clear()
        deletePlaceQueue.clear()
    }

    suspend fun flushDeleteQueueAndAwait() {
        val remainingQueue = deletePlaceQueue.toList()
        clearDeleteSession()

        remainingQueue
            .map { place ->
                flushScope.async {
                    turipRepository.deleteTuripPlace(
                        uiState.value.selectedTurip.id,
                        place.placeId,
                    )
                }
            }.awaitAll()
    }

    fun updateScreenMode(turipPlaceScreenMode: TuripPlaceScreenMode) {
        if (turipPlaceScreenMode == TuripPlaceScreenMode.MoreOption) {
            _uiState.update { it.copy(inputTuripName = "") }
        }

        _uiState.update { it.copy(screenMode = turipPlaceScreenMode) }
    }

    fun updateInputName(name: String) {
        if (name.length > MAX_NAME_LENGTH) return
        val editModel: ImmutableList<TuripEditModel> = _uiState.value.editModels
        val status: TuripNameStatusModel = TuripNameStatusModel.of(name, editModel)
        _uiState.update {
            it.copy(
                inputTuripName = name,
                turipNameStatus = status,
            )
        }
    }

    fun updateTuripName() {
        viewModelScope.launch {
            turipRepository
                .updateTurip(uiState.value.selectedTurip.id, uiState.value.inputTuripName)
                .onSuccess {
                    _uiState.update { state: TuripDetailUiState ->
                        state.copy(
                            isLoading = false,
                            errorUiState = ErrorUiState.None,
                            selectedTurip = uiState.value.selectedTurip.copy(name = uiState.value.inputTuripName),
                            inputTuripName = "",
                        )
                    }
                    _uiEffect.send(TuripDetailUiEffect.TuripUpdated)
                }.onFailure { errorType: ErrorType ->
                    if (errorType == ErrorType.Turip.DuplicatedName) {
                        _uiState.update {
                            it.copy(
                                turipNameStatus = TuripNameStatusModel.DUPLICATE_NAME,
                            )
                        }
                    } else {
                        sendErrorEffect(errorType, TuripPlaceRetryAction.TuripNameUpdate)
                    }
                }
        }
    }

    fun updateSelectedPlace(placeId: Long) {
        _uiState.update { turipDetailUiState: TuripDetailUiState ->
            turipDetailUiState.copy(
                selectedPlace =
                    _uiState.value.placesLatLng.find { it.placeId == placeId }
                        ?: throw IllegalStateException("장소를 찾을 수 없습니다."),
            )
        }
    }

    fun showBottomSheet() = _uiState.update { it.copy(showBottomSheet = true) }

    fun dismissBottomSheet() =
        _uiState.update {
            it.copy(
                showBottomSheet = false,
                screenMode = TuripPlaceScreenMode.MoreOption,
            )
        }

    fun showMemberBottomSheet() = _uiState.update { it.copy(showMemberBottomSheet = true) }

    fun dismissMemberBottomSheet() = _uiState.update { it.copy(showMemberBottomSheet = false) }

    fun showTuripRemoveDialog() = _uiState.update { it.copy(showTuripRemoveDialog = true) }

    fun dismissTuripRemoveDialog() = _uiState.update { it.copy(showTuripRemoveDialog = false) }

    fun deleteTurip() {
        viewModelScope.launch {
            deleteTuripUseCase(
                turipId = selectedTuripId,
                type = uiState.value.selectedTurip.type,
            ).onSuccess {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorUiState = ErrorUiState.None,
                    )
                }
                _uiEffect.send(TuripDetailUiEffect.TuripDelete)
            }.onFailure { errorType ->
                sendErrorEffect(errorType, TuripPlaceRetryAction.TuripDelete)
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun registerDragEndEvents() {
        dragEndEvents
            .debounce(500L)
            .onEach { updateTuripPlacesOrder(uiState.value.places) }
            .launchIn(viewModelScope)
    }

    private fun updateTuripPlacesOrder(reorderedTuripPlaces: ImmutableList<TuripPlaceModel>) {
        viewModelScope.launch {
            turipRepository
                .updateTuripPlacesOrder(
                    turipId = _uiState.value.selectedTurip.id,
                    updatedOrder = reorderedTuripPlaces.map { it.turipPlaceId },
                ).onSuccess {
                    _uiState.update { it.copy(places = reorderedTuripPlaces) }
                    clearReorderSnapshot()
                    Timber.d("장소 순서 변경 API 성공")
                }.onFailure {
                    rollbackReorderedPlaces()
                    _uiEffect.send(
                        TuripDetailUiEffect.ShowReorderDetailFailed(
                            retryAction =
                                TuripPlaceRetryAction.UpdateReorderedPlaces(
                                    reorderedTuripPlaces,
                                ),
                        ),
                    )
                    Timber.e("장소 순서 변경 API 실패")
                }
        }
    }

    fun shareTuripInvitationLink() {
        when (sessionState.value) {
            SessionState.Member -> {
                viewModelScope.launch {
                    turipRepository
                        .createInvitationToken(selectedTuripId)
                        .onSuccess { token: TuripInvitationToken ->
                            _uiEffect.send(
                                TuripDetailUiEffect.ShareTuripInvitationLink(
                                    invitationLink = token.toUrl(),
                                ),
                            )
                        }.onFailure { errorType ->
                            sendErrorEffect(
                                errorType = errorType,
                                retryAction = TuripPlaceRetryAction.ShareTuripInvitationLink,
                            )
                        }
                }
            }

            SessionState.Guest, SessionState.Uninitialized -> {
                viewModelScope.launch {
                    _uiEffect.send(TuripDetailUiEffect.ShowTuripShareNotAllowed)
                }
            }
        }
    }

    fun shareTuripByText() {
        when (sessionState.value) {
            SessionState.Member -> {
                val turipShareModel =
                    TuripShareModel(
                        name = uiState.value.selectedTurip.name,
                        places = uiState.value.places.map { it.toUiModel() },
                    )
                viewModelScope.launch {
                    _uiEffect.send(TuripDetailUiEffect.ShareTuripByText(turipShareModel))
                }
            }

            SessionState.Guest, SessionState.Uninitialized -> {
                viewModelScope.launch {
                    _uiEffect.send(TuripDetailUiEffect.ShowTuripShareNotAllowed)
                }
            }
        }
    }

    private suspend fun handleGlobalError(uiError: UiError.Global) {
        when (uiError) {
            UiError.Global.Network -> {
                _uiState.update {
                    it.copy(isLoading = false, errorUiState = ErrorUiState.Network)
                }
            }

            UiError.Global.Server -> {
                _uiState.update {
                    it.copy(isLoading = false, errorUiState = ErrorUiState.Server)
                }
            }

            UiError.Global.TokenExpired -> {
                _uiState.update { it.copy(isLoading = false) }
                _uiEffect.send(TuripDetailUiEffect.NavigateToLogin)
            }
        }
    }

    fun handleErrorRetryRequest(action: TuripPlaceRetryAction) {
        when (action) {
            is TuripPlaceRetryAction.UpdateTuripPlace -> {
                loadPlaces(action.placeId)
            }

            is TuripPlaceRetryAction.UpdateReorderedPlaces -> {
                updateTuripPlacesOrder(action.reorderedPlaces)
            }

            TuripPlaceRetryAction.TuripDelete -> {
                deleteTurip()
            }

            TuripPlaceRetryAction.TuripNameUpdate -> {
                updateTuripName()
            }

            TuripPlaceRetryAction.ShareTuripInvitationLink -> {
                shareTuripInvitationLink()
            }

            TuripPlaceRetryAction.StreamConnectionLost -> {
                loadPlaces(selectedTuripId)
                viewModelScope.launch {
                    loadSelectedTurip(selectedTuripId)
                    if (uiState.value.selectedTurip.type == TuripTypeModel.TOGETHER) {
                        observeTuripStream(selectedTuripId)
                    }
                }
            }
        }
    }

    private fun rollbackReorderedPlaces() {
        val snapshot: ImmutableList<TuripPlaceModel> = reorderPlacesSnapshot ?: return
        _uiState.update { state ->
            state.copy(
                places = snapshot,
                placesLatLng = snapshot.map { it.toPlaceLatLngUiModel() }.toImmutableList(),
            )
        }
        clearReorderSnapshot()
    }

    private fun TuripPlaceModel.toPlaceLatLngUiModel(): PlaceLatLngUiModel =
        PlaceLatLngUiModel(
            placeId = placeId,
            name = name,
            latLng = latLng,
        )

    private fun clearSnapshots() {
        clearDeleteSnapshot()
        clearReorderSnapshot()
    }

    private fun clearDeleteSnapshot() {
        deleteTuripPlaceSnapshot = DeleteTuripPlaceSnapshot.EMPTY
    }

    private fun clearReorderSnapshot() {
        reorderPlacesSnapshot = null
    }

    // API 호출 실패 시 롤백을 위해 원본 상태 기록
    // 장소 제거 API가 반영되지 않은 상태라면 제거 전 원본 상태를 기록
    fun onDragStart() {
        reorderPlacesSnapshot =
            if (deleteTuripPlaceSnapshot.hasSnapshot) deleteTuripPlaceSnapshot.originPlaces else uiState.value.places
    }

    // 드래그 시 아이템 위치 변경
    fun onDragMove(
        from: Int,
        to: Int,
    ) {
        if (from == to) return
        _uiState.update { state ->
            val reOrderedPlaces =
                state.places
                    .toMutableList()
                    .apply { add(to, removeAt(from)) }
                    .toImmutableList()
            state.copy(places = reOrderedPlaces)
        }
    }

    // 드래그 후 데이터가 변경되었을 때만 tryEmit
    fun onDragEnd() {
        val current = uiState.value.places
        if (reorderPlacesSnapshot == current) return

        dragEndEvents.tryEmit(Unit)
    }

    private suspend fun sendErrorEffect(
        errorType: ErrorType,
        retryAction: TuripPlaceRetryAction,
    ) {
        _uiState.update { it.copy(isLoading = false) }
        val uiError: UiError = errorType.toUiError()
        if (uiError is UiError.Global) {
            when (uiError) {
                UiError.Global.Network -> {
                    _uiEffect.send(TuripDetailUiEffect.ShowError(ErrorUiState.Network, retryAction))
                }

                UiError.Global.Server -> {
                    _uiEffect.send(TuripDetailUiEffect.ShowError(ErrorUiState.Server, retryAction))
                }

                UiError.Global.TokenExpired -> {
                    _uiEffect.send(TuripDetailUiEffect.NavigateToLogin)
                }
            }
        }
    }

    companion object {
        private const val INVALID_ID = -1L
        private const val MAX_NAME_LENGTH = 20
        private const val UNSTABLE_NETWORK_RETRY_THRESHOLD = 2
    }
}
