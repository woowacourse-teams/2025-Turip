package com.on.turip.feature.trip.impl.turipselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.core.data.session.SessionManager
import com.on.turip.core.domain.repository.TuripRepository
import com.on.turip.core.domain.session.SessionState
import com.on.turip.core.model.bookmark.TuripPlace
import com.on.turip.core.model.result.ErrorType
import com.on.turip.core.model.result.onFailure
import com.on.turip.core.model.result.onSuccess
import com.on.turip.core.model.turip.Turip
import com.on.turip.core.model.turip.TuripInvitationToken
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.core.ui.error.UiError
import com.on.turip.core.ui.error.toUiError
import com.on.turip.core.ui.model.turip.TuripShareModel
import com.on.turip.feature.trip.impl.model.MapModel
import com.on.turip.feature.trip.impl.turipselection.model.DeletePlaceSnapshot
import com.on.turip.feature.trip.impl.turipselection.model.TuripPlaceModel
import io.github.aakira.napier.Napier
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
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class PlaceTuripSelectionViewModel(
    private val turipRepository: TuripRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {
    private var originTuripIds: Set<Long> = setOf()
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
        observeTuripChanges()
    }

    private fun observeTuripChanges() {
        turipRepository.turips
            .drop(1)
            .onEach {
                val state = uiState.value
                if (state.screenMode is PlaceTuripSelectionScreenMode.Turips && state.selectionPlaceId != 0L) {
                    loadTuripsByPlace(state.selectionPlaceId, state.placeName)
                }
            }.launchIn(viewModelScope)
    }

    fun loadTuripsByPlace(
        placeId: Long,
        placeName: String,
    ) {
        viewModelScope.launch {
            turipRepository
                .loadTuripsByPlaceId(placeId)
                .onSuccess { turips ->
                    val uiModels = turips.map { it.toSelectionModel() }.toImmutableList()

                    _uiState.update { state ->
                        state.copy(
                            selectionPlaceId = placeId,
                            screenMode = PlaceTuripSelectionScreenMode.Turips,
                            placeName = placeName,
                            turips = uiModels,
                            selectedTuripPlaces = persistentListOf(),
                            isChanged = false,
                        )
                    }

                    originTuripIds =
                        uiModels
                            .filter { it.isSelected }
                            .map { it.id }
                            .toSet()
                    Napier.d("장소에 대한 튜립 목록 바텀시트, 튜립 목록 불러오기 성공")
                }.onFailure { errorType ->
                    sendErrorEffect(
                        errorType = errorType,
                        retryAction = PlaceTuripSelectionRetryAction.LoadTurips(placeId, placeName),
                    )
                    Napier.e("장소에 대한 튜립 목록 바텀시트, 튜립 목록 불러오기 실패")
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

    fun updateTurip(turipModel: TuripSelectionModel) {
        val updateHasTuripPlace = !turipModel.isSelected
        _uiState.update { state ->
            val updatedTurips =
                state.turips
                    .map { turip ->
                        if (turip.id == turipModel.id) turip.copy(isSelected = updateHasTuripPlace) else turip
                    }.toImmutableList()

            state.copy(
                turips = updatedTurips,
                isChanged = isTuripChanged(updatedTurips),
            )
        }
    }

    fun updateTuripsByPlace() {
        viewModelScope.launch {
            val selectedTuripIds = uiState.value.turips.filter { it.isSelected }.map { it.id }
            val placeId = uiState.value.selectionPlaceId

            turipRepository
                .updatePlaceTurips(placeId, selectedTuripIds, originTuripIds)
                .onSuccess {
                    _uiEffect.send(
                        PlaceTuripSelectionUiEffect.UpdateTuripsByPlace(
                            placeId = placeId,
                            hasTurip = selectedTuripIds.isNotEmpty(),
                        ),
                    )
                    _uiEffect.send(PlaceTuripSelectionUiEffect.Dismiss)
                    Napier.d("바텀시트, 선택한 장소에 대한 튜립들 현황 업데이트 성공")
                }.onFailure { errorType ->
                    sendErrorEffect(
                        errorType = errorType,
                        retryAction = PlaceTuripSelectionRetryAction.UpdateTuripsByPlace,
                    )
                    Napier.e("바텀시트, 선택한 장소에 대한 튜립들 현황 업데이트 실패")
                }
        }
    }

    private fun isTuripChanged(turips: List<TuripSelectionModel>): Boolean {
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

    fun loadPlacesInSelectTurip(turipModel: TuripSelectionModel) {
        viewModelScope.launch {
            turipRepository
                .loadTuripPlaces(turipModel.id)
                .onSuccess { turipPlaces: List<TuripPlace> ->
                    _uiState.update { state ->
                        state.copy(
                            screenMode = PlaceTuripSelectionScreenMode.TuripDetail(turipModel),
                            selectedTuripPlaces = turipPlaces.map { it.toSelectionPlaceModel() }.toImmutableList(),
                        )
                    }
                }.onFailure { errorType ->
                    sendErrorEffect(
                        errorType = errorType,
                        retryAction = PlaceTuripSelectionRetryAction.LoadPlacesInTurip(turipModel),
                    )
                    Napier.e("튜립에 담긴 장소들을 불러오는 API 호출 실패 turipName = ${turipModel.name}")
                }
        }
    }

    fun applyTuripPlaceDelete(place: TuripPlaceModel) {
        if (deletePlaceSnapshot.hasSnapshot()) return

        _uiState.update { state ->
            deletePlaceSnapshot = DeletePlaceSnapshot(place, state.selectedTuripPlaces)
            val updatedPlaces =
                state.selectedTuripPlaces
                    .filter { it.turipPlaceId != place.turipPlaceId }
                    .toImmutableList()
            state.copy(selectedTuripPlaces = updatedPlaces)
        }
        viewModelScope.launch {
            _uiEffect.send(PlaceTuripSelectionUiEffect.ShowTuripPlaceRemoved(placeName = place.name))
        }
    }

    fun rollbackTuripPlaceDelete() {
        if (deletePlaceSnapshot.hasSnapshot()) {
            _uiState.update { it.copy(selectedTuripPlaces = deletePlaceSnapshot.originPlaces) }
            deletePlaceSnapshot = DeletePlaceSnapshot.EMPTY
        }
    }

    fun commitTuripPlaceDelete() {
        viewModelScope.launch { commitTuripPlaceDeleteApi() }
    }

    private val commitMutex = Mutex()

    private suspend fun commitTuripPlaceDeleteApi() {
        commitMutex.withLock {
            if (!deletePlaceSnapshot.hasSnapshot()) {
                Napier.e("제거할 장소에 대한 정보가 없어요. deletePlaceSnapshot을 확인 해주세요")
                return
            }

            val deletePlace = deletePlaceSnapshot.deletePlace
            val screenMode = uiState.value.screenMode
            if (screenMode is PlaceTuripSelectionScreenMode.TuripDetail) {
                turipRepository
                    .deleteTuripPlace(screenMode.turipModel.id, deletePlace.placeId)
                    .onSuccess {
                        syncTuripForSelectedPlace(deletePlace, screenMode)
                        Napier.d("튜립 상세 바텀시트, 장소 업데이트 성공")
                    }.onFailure {
                        _uiEffect.send(PlaceTuripSelectionUiEffect.ShowTuripPlaceRemoveFailed(deletePlace.name))
                        _uiState.update { it.copy(selectedTuripPlaces = deletePlaceSnapshot.originPlaces) }
                        Napier.e("튜립 상세 바텀시트, 장소 업데이트 실패 place = ${deletePlace.name}")
                    }

                deletePlaceSnapshot = DeletePlaceSnapshot.EMPTY
            }
        }
    }

    private suspend fun syncTuripForSelectedPlace(
        deletePlace: TuripPlaceModel,
        screenMode: PlaceTuripSelectionScreenMode.TuripDetail,
    ) {
        if (uiState.value.selectionPlaceId == deletePlace.placeId) {
            _uiState.update { state ->
                val syncedTurips =
                    state.turips
                        .map { if (it.id == screenMode.turipModel.id) it.copy(isSelected = false) else it }
                        .toImmutableList()

                state.copy(turips = syncedTurips)
            }
            originTuripIds = originTuripIds.toMutableSet().apply { remove(screenMode.turipModel.id) }.toSet()

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
                                _uiEffect.send(PlaceTuripSelectionUiEffect.ShareTuripInvitationLink(token.toUrl()))
                            }.onFailure { errorType ->
                                sendErrorEffect(
                                    errorType = errorType,
                                    retryAction = PlaceTuripSelectionRetryAction.ShareTuripInvitationLink,
                                )
                            }
                    }
                }

                SessionState.Guest,
                SessionState.Uninitialized,
                -> {
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
                            places = uiState.value.selectedTuripPlaces.map { it.toShareModel() },
                        )
                    viewModelScope.launch {
                        _uiEffect.send(PlaceTuripSelectionUiEffect.ShareTuripByText(turipShareModel))
                    }
                }

                SessionState.Guest,
                SessionState.Uninitialized,
                -> {
                    viewModelScope.launch {
                        _uiEffect.send(PlaceTuripSelectionUiEffect.TuripShareNotAllowed)
                    }
                }
            }
        }
    }

    fun onDragStart() {
        reorderPlacesSnapshot =
            if (deletePlaceSnapshot.hasSnapshot()) {
                deletePlaceSnapshot.originPlaces
            } else {
                uiState.value.selectedTuripPlaces
            }
    }

    fun onDragMove(
        from: Int,
        to: Int,
    ) {
        if (from == to) return
        _uiState.update { state ->
            val reorderedPlaces =
                state.selectedTuripPlaces
                    .toMutableList()
                    .apply { add(to, removeAt(from)) }
                    .toImmutableList()
            state.copy(selectedTuripPlaces = reorderedPlaces)
        }
    }

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
                        Napier.d("장소 순서 변경 API 성공")
                    }.onFailure { errorType ->
                        if (errorType is ErrorType.Auth) {
                            sessionManager.switchToGuest()
                            _uiEffect.send(PlaceTuripSelectionUiEffect.NavigateToLogin)
                            return@launch
                        }

                        reorderPlacesSnapshot?.let { snapshot ->
                            _uiState.update { it.copy(selectedTuripPlaces = snapshot) }
                        }
                        _uiEffect.send(
                            PlaceTuripSelectionUiEffect.ShowReorderPlaceFailed(
                                retryAction = PlaceTuripSelectionRetryAction.UpdateReorderedPlaces(reorderedTuripPlaces),
                            ),
                        )
                        Napier.e("장소 순서 변경 API 실패")
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

    fun handleErrorRetryRequest(action: PlaceTuripSelectionRetryAction) {
        when (action) {
            is PlaceTuripSelectionRetryAction.LoadTurips -> loadTuripsByPlace(action.placeId, action.placeName)
            is PlaceTuripSelectionRetryAction.UpdateTurip -> updateTurip(action.turipModel)
            is PlaceTuripSelectionRetryAction.LoadPlacesInTurip -> loadPlacesInSelectTurip(action.turipModel)
            PlaceTuripSelectionRetryAction.ShareTuripInvitationLink -> shareTuripInvitationLink()
            is PlaceTuripSelectionRetryAction.UpdateReorderedPlaces -> updateTuripPlacesOrder(action.reorderedPlaces)
            PlaceTuripSelectionRetryAction.UpdateTuripsByPlace -> updateTuripsByPlace()
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
                    _uiEffect.send(PlaceTuripSelectionUiEffect.ShowError(ErrorUiState.Network, retryAction))
                }

                UiError.Global.Server -> {
                    _uiEffect.send(PlaceTuripSelectionUiEffect.ShowError(ErrorUiState.Server, retryAction))
                }

                UiError.Global.TokenExpired -> {
                    sessionManager.switchToGuest()
                    _uiEffect.send(PlaceTuripSelectionUiEffect.NavigateToLogin)
                }
            }
        }
    }

    private fun Turip.toSelectionModel(): TuripSelectionModel =
        TuripSelectionModel(
            id = id,
            name = name,
            placeCount = placeCount,
            isSelected = hasIncludePlace,
            isDefault = isDefault,
        )

    private fun TuripPlace.toSelectionPlaceModel(): TuripPlaceModel =
        TuripPlaceModel(
            turipPlaceId = id,
            placeId = place.placeId,
            order = order,
            name = place.name,
            category = place.category.firstOrNull().orEmpty(),
            mapModel = MapModel.from(place.url),
            isTuripPlace = true,
            latitude = place.latitude,
            longitude = place.longitude,
        )

    private fun TuripInvitationToken.toUrl(): String =
        "https://$APP_LINK_TURIP_INVITATION_HOST/invitations?token=${value.encodeUrlParameter()}"

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

    private companion object {
        private const val APP_LINK_TURIP_INVITATION_HOST = "invite.turip.kro.kr"
    }
}
