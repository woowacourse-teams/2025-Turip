package com.on.turip.feature.turipdetail.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.core.data.session.SessionManager
import com.on.turip.core.domain.repository.TuripRepository
import com.on.turip.core.domain.session.SessionState
import com.on.turip.core.domain.usecase.DeleteTuripUseCase
import com.on.turip.core.domain.usecase.ObserveTuripStreamUseCase
import com.on.turip.core.model.result.ErrorType
import com.on.turip.core.model.result.onFailure
import com.on.turip.core.model.result.onSuccess
import com.on.turip.core.model.turip.Turip
import com.on.turip.core.model.turip.TuripInvitationToken
import com.on.turip.core.model.turip.TuripMember
import com.on.turip.core.model.turip.TuripStreamEvent
import com.on.turip.core.model.turip.TuripType
import com.on.turip.core.model.turip.result.TuripStreamResult
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.core.ui.error.UiError
import com.on.turip.core.ui.error.toUiError
import com.on.turip.core.ui.model.namestatus.TuripNameStatusModel
import com.on.turip.core.ui.model.turip.TuripEditModel
import com.on.turip.core.ui.model.turip.TuripShareModel
import com.on.turip.feature.turipdetail.impl.mapper.toEditModel
import com.on.turip.feature.turipdetail.impl.mapper.toPlaceLatLngUiModel
import com.on.turip.feature.turipdetail.impl.mapper.toShareModel
import com.on.turip.feature.turipdetail.impl.mapper.toUiModel
import com.on.turip.feature.turipdetail.impl.mapper.toUiMyTuripModel
import com.on.turip.feature.turipdetail.impl.model.RefreshScope
import com.on.turip.feature.turipdetail.impl.model.TuripPlaceModel
import com.on.turip.feature.turipdetail.impl.model.turip.PlaceLatLngUiModel
import io.github.aakira.napier.Napier
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
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
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

class TuripDetailViewModel(
    private val turipRepository: TuripRepository,
    private val deleteTuripUseCase: DeleteTuripUseCase,
    private val observeTuripStreamUseCase: ObserveTuripStreamUseCase,
    private val sessionManager: SessionManager,
) : ViewModel() {
    private val _uiState: MutableStateFlow<TuripDetailUiState> =
        MutableStateFlow(TuripDetailUiState.Idle)
    val uiState: StateFlow<TuripDetailUiState> = _uiState.asStateFlow()

    private val _uiEffect: Channel<TuripDetailUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<TuripDetailUiEffect> = _uiEffect.receiveAsFlow()

    private val sessionState: StateFlow<SessionState> = sessionManager.state
    private var reorderPlacesSnapshot: ImmutableList<TuripPlaceModel>? = null
    private var selectedTuripId: Long = INVALID_ID
    private var isNetworkUnstable: Boolean = false

    private val dragEndEvents = MutableSharedFlow<Unit>(replay = 0, extraBufferCapacity = 1)
    private val refreshTrigger = MutableStateFlow(RefreshScope(turip = false, places = false))

    private val deletePlaceQueue = ArrayDeque<TuripPlaceModel>()
    private val committedPlaceIds = mutableSetOf<Long>()
    private var originBeforeDeleteSession: Pair<ImmutableList<TuripPlaceModel>, ImmutableList<PlaceLatLngUiModel>>? =
        null

    private val commitMutex = Mutex()

    init {
        registerDragEndEvents()
        registerStreamRefresh()
    }

    fun initIfNeeded(turipId: Long) {
        if (turipId == INVALID_ID) return
        if (selectedTuripId == turipId && uiState.value.selectedTurip.id == turipId) return

        selectedTuripId = turipId
        isNetworkUnstable = false
        loadTuripData(turipId)
    }

    private fun loadTuripData(turipId: Long) {
        viewModelScope.launch {
            loadSelectedTurip(turipId)
            if (uiState.value.selectedTurip.type == TuripType.TOGETHER) {
                observeTuripStream(turipId)
            }
        }
        loadPlaces(turipId)
    }

    fun loadTurip(selectedTuripId: Long) {
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
                .onSuccess { result ->
                    val serverPlaces = result.map { it.toUiModel() }

                    val mergedPlaces =
                        if (deletePlaceQueue.isNotEmpty()) {
                            val deletingIds = deletePlaceQueue.map { it.placeId }.toSet()
                            serverPlaces.filter { it.placeId !in deletingIds }
                        } else {
                            serverPlaces
                        }

                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            errorUiState = ErrorUiState.None,
                            places = mergedPlaces.toImmutableList(),
                            placesLatLng =
                                mergedPlaces
                                    .map { it.toPlaceLatLngUiModel() }
                                    .toImmutableList(),
                            selectedPlace =
                                mergedPlaces
                                    .firstOrNull()
                                    ?.toPlaceLatLngUiModel()
                                    ?: PlaceLatLngUiModel.Idle,
                        )
                    }

                    if (deletePlaceQueue.isNotEmpty()) {
                        originBeforeDeleteSession = serverPlaces.toImmutableList() to
                            serverPlaces.map { it.toPlaceLatLngUiModel() }.toImmutableList()
                    } else {
                        clearSnapshots()
                    }
                }
        }
    }

    private fun loadMembers() {
        viewModelScope.launch {
            turipRepository
                .loadTuripMembers(selectedTuripId)
                .onSuccess { members: List<TuripMember> ->
                    _uiState.update {
                        it.copy(members = members.map { member -> member.nickName }.toImmutableList())
                    }
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
                            Napier.d("SSE 재연결 중. turipId=$turipId, retryCount=${result.retryCount}")
                            if (result.retryCount >= UNSTABLE_NETWORK_RETRY_THRESHOLD && !isNetworkUnstable) {
                                isNetworkUnstable = true
                                _uiEffect.send(TuripDetailUiEffect.ShowNetworkUnstable)
                            }
                        }

                        TuripStreamResult.Fatal.TokenExpired -> {
                            sessionManager.switchToGuest()
                            _uiEffect.send(TuripDetailUiEffect.NavigateToLogin)
                        }

                        TuripStreamResult.Fatal.Forbidden -> {
                            Napier.e("SSE 권한 없음, 스트림 중단. turipId=$turipId")
                        }

                        is TuripStreamResult.Fatal.ConnectionLost -> {
                            Napier.e("SSE 최대 재시도 초과, 스트림 종료. turipId=$turipId")
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
                Napier.d("튜립 SSE 연결 성공: turipId=${event.turipId}, eventId=${event.id}")
                if (isNetworkUnstable) {
                    isNetworkUnstable = false
                    viewModelScope.launch {
                        _uiEffect.send(TuripDetailUiEffect.ShowNetworkRecovered)
                    }
                }
                requestRefresh(turip = true, places = true)
                loadMembers()
            }

            is TuripStreamEvent.FolderUpdate -> {
                Napier.d("튜립 SSE 폴더 업데이트 수신: turipId=${event.turipId}, action=${event.action}, eventId=${event.id}")
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
                Napier.d("튜립 SSE 멤버 업데이트 수신: turipId=${event.turipId}, action=${event.action}, memberCount=${event.memberCount}, eventId=${event.id}")
                requestRefresh(turip = true, places = false)
                loadMembers()
            }

            is TuripStreamEvent.Heartbeat -> {
                Napier.v("튜립 SSE 하트비트 수신: eventId=${event.id}")
            }
        }
    }

    private fun requestRefresh(
        turip: Boolean,
        places: Boolean,
    ) {
        refreshTrigger.update { current ->
            RefreshScope(
                turip = current.turip || turip,
                places = current.places || places,
            )
        }
    }

    @OptIn(FlowPreview::class)
    private fun registerStreamRefresh() {
        refreshTrigger
            .debounce(300L)
            .onEach { scope: RefreshScope ->
                when {
                    scope.turip && scope.places -> {
                        loadSelectedTurip(selectedTuripId)
                        loadPlaces(selectedTuripId)
                    }

                    scope.turip -> {
                        loadSelectedTurip(selectedTuripId)
                    }

                    scope.places -> {
                        loadPlaces(selectedTuripId)
                    }

                    else -> {
                        return@onEach
                    }
                }
                refreshTrigger.value = RefreshScope(turip = false, places = false)
            }.launchIn(viewModelScope)
    }

    fun applyTuripPlaceDelete(placeId: Long) {
        val targetPlace =
            uiState.value.places.find { it.placeId == placeId }
                ?: run {
                    Napier.e("삭제할 장소를 찾을 수 없어요. placeId = $placeId")
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
                    }.onFailure { errorType ->
                        if (errorType is ErrorType.Auth) {
                            sessionManager.switchToGuest()
                            _uiEffect.send(TuripDetailUiEffect.NavigateToLogin)
                            return@onFailure
                        }

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
        val flushed =
            withTimeoutOrNull(FLUSH_DELETE_TIMEOUT_MILLIS) {
                commitMutex.withLock {
                    val remainingQueue = deletePlaceQueue.toList()
                    clearDeleteSession()

                    withContext(Dispatchers.Default) {
                        remainingQueue.forEach { place ->
                            runCatching {
                                turipRepository.deleteTuripPlace(
                                    uiState.value.selectedTurip.id,
                                    place.placeId,
                                )
                            }
                        }
                    }
                }
                true
            } ?: false

        if (!flushed) {
            Napier.w("삭제 큐 flush 타임아웃. turipId=$selectedTuripId")
        }
    }

    fun syncMemberCountToCachedTurips() {
        val turipId = uiState.value.selectedTurip.id
        if (turipId == INVALID_ID) return

        val currentMembers = uiState.value.members
        val memberCount =
            if (currentMembers.isNotEmpty()) {
                currentMembers.size
            } else {
                uiState.value.selectedTurip.memberCount
            }

        turipRepository.updateCachedTuripMemberCount(
            turipId = turipId,
            memberCount = memberCount,
        )
    }

    fun updateScreenMode(turipPlaceScreenMode: TuripPlaceScreenMode) {
        _uiState.update {
            if (turipPlaceScreenMode == TuripPlaceScreenMode.Edit) {
                it.copy(
                    screenMode = turipPlaceScreenMode,
                    inputTuripName = "",
                    turipNameStatus = TuripNameStatusModel.EMPTY,
                )
            } else {
                it.copy(screenMode = turipPlaceScreenMode)
            }
        }
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
                        _uiState.update { it.copy(turipNameStatus = TuripNameStatusModel.DUPLICATE_NAME) }
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

    fun showMoreOptionBottomSheet() = _uiState.update { it.copy(showMoreOptionBottomSheet = true) }

    fun dismissMoreOptionBottomSheet() =
        _uiState.update {
            it.copy(
                showMoreOptionBottomSheet = false,
                screenMode = TuripPlaceScreenMode.MoreOption,
            )
        }

    fun showMemberBottomSheet() = _uiState.update { it.copy(showMemberBottomSheet = true) }

    fun dismissMemberBottomSheet() = _uiState.update { it.copy(showMemberBottomSheet = false) }

    fun showTuripRemoveDialog() = _uiState.update { it.copy(showTuripRemoveDialog = true) }

    fun dismissTuripRemoveDialog() = _uiState.update { it.copy(showTuripRemoveDialog = false) }

    fun deleteTurip() {
        viewModelScope.launch {
            val selectedTurip = uiState.value.selectedTurip
            if (selectedTuripId == INVALID_ID || selectedTurip.id != selectedTuripId) {
                Napier.e("튜립 정보가 아직 초기화되지 않아 삭제를 중단합니다. turipId=$selectedTuripId")
                return@launch
            }

            deleteTuripUseCase(
                turipId = selectedTurip.id,
                type = selectedTurip.type,
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
                    Napier.d("장소 순서 변경 API 성공")
                }.onFailure { errorType ->
                    if (errorType is ErrorType.Auth) {
                        sessionManager.switchToGuest()
                        _uiEffect.send(TuripDetailUiEffect.NavigateToLogin)
                        return@onFailure
                    }

                    rollbackReorderedPlaces()
                    _uiEffect.send(
                        TuripDetailUiEffect.ShowReorderDetailFailed(
                            retryAction =
                                TuripPlaceRetryAction.UpdateReorderedPlaces(reorderedTuripPlaces),
                        ),
                    )
                    Napier.e("장소 순서 변경 API 실패")
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
                            turipRepository.updateCachedTuripSharedStatus(
                                turipId = selectedTuripId,
                                isShared = true,
                            )
                            _uiState.update { state ->
                                state.copy(
                                    selectedTurip = state.selectedTurip.copy(type = TuripType.TOGETHER),
                                )
                            }
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
                        places = uiState.value.places.map { it.toShareModel() },
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
                    if (uiState.value.selectedTurip.type == TuripType.TOGETHER) {
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

    private fun clearSnapshots() {
        clearReorderSnapshot()
    }

    private fun clearReorderSnapshot() {
        reorderPlacesSnapshot = null
    }

    fun onDragStart() {
        reorderPlacesSnapshot = uiState.value.places
    }

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
                    sessionManager.switchToGuest()
                    _uiEffect.send(TuripDetailUiEffect.NavigateToLogin)
                }
            }
        }
    }

    private fun TuripInvitationToken.toUrl(): String =
        "https://$APP_LINK_TURIP_INVITATION_HOST?token=${value.encodeUrlParameter()}"

    private fun String.encodeUrlParameter(): String =
        encodeToByteArray().joinToString(separator = "") { byte ->
            val value = byte.toInt() and 0xFF
            val char = value.toChar()
            if (char.isUrlParameterSafe()) {
                char.toString()
            } else {
                "%${value.toString(16).uppercase().padStart(2, '0')}"
            }
        }

    private fun Char.isUrlParameterSafe(): Boolean =
        this in 'A'..'Z' ||
            this in 'a'..'z' ||
            this in '0'..'9' ||
            this == '-' ||
            this == '_' ||
            this == '.' ||
            this == '~'

    companion object {
        private const val INVALID_ID = -1L
        private const val MAX_NAME_LENGTH = 20
        private const val UNSTABLE_NETWORK_RETRY_THRESHOLD = 2
        private const val FLUSH_DELETE_TIMEOUT_MILLIS = 3_000L
        private const val APP_LINK_TURIP_INVITATION_HOST = "invite.turip.kro.kr"
    }
}
