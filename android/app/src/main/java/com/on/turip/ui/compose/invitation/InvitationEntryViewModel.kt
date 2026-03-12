package com.on.turip.ui.compose.invitation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.core.result.ErrorType
import com.on.turip.domain.invitation.usecase.DetermineInvitationEntryRouteUseCase
import com.on.turip.domain.invitation.usecase.model.InvitationEntryResult
import com.on.turip.domain.session.SessionState
import com.on.turip.domain.session.SessionStore
import com.on.turip.ui.compose.invitation.util.InvitationTokenParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvitationEntryViewModel @Inject constructor(
    private val determineInvitationEntryRouteUseCase: DetermineInvitationEntryRouteUseCase,
    sessionStore: SessionStore,
) : ViewModel() {
    private val _uiState: MutableStateFlow<InvitationEntryUiState> =
        MutableStateFlow(InvitationEntryUiState.Idle)
    val uiState: StateFlow<InvitationEntryUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<InvitationEntryUiEffect>(Channel.BUFFERED)
    val uiEffect: Flow<InvitationEntryUiEffect> = _uiEffect.receiveAsFlow()

    private val sessionState: StateFlow<SessionState> = sessionStore.state

    fun initDeepLinkUrl(deepLinkUrl: String?) {
        _uiState.update { it.copy(deepLinkUrl = deepLinkUrl?.trim()?.takeIf(String::isNotEmpty)) }
    }

    fun resolveInvitationEntry() {
        viewModelScope.launch {
            val invitationToken: String? = InvitationTokenParser.extractTokenFromUrl(_uiState.value.deepLinkUrl)
            when (
                val result: InvitationEntryResult =
                    determineInvitationEntryRouteUseCase(invitationToken)
            ) {
                is InvitationEntryResult.MemberValidated -> {
                    val isJoinedTurip = result.invitationInformation.alreadyJoined
                    if (isJoinedTurip) {
                        _uiState.update {
                            it.copy(
                                invitationTuripId = null,
                                invitationTuripName = null,
                                dialogState = null,
                            )
                        }
                        _uiEffect.send(
                            InvitationEntryUiEffect.NavigateToTuripDetail(result.invitationInformation.turipId),
                        )
                    } else {
                        _uiState.update {
                            it.copy(
                                invitationTuripId = result.invitationInformation.turipId,
                                invitationTuripName = result.turipName,
                                dialogState = null,
                            )
                        }
                    }
                }

                InvitationEntryResult.RequiresAuth -> {
                    _uiEffect.send(InvitationEntryUiEffect.NavigateToLogin)
                }

                InvitationEntryResult.InvalidInvitationToken -> {
                    val target =
                        when (sessionState.value) {
                            SessionState.Member -> InvalidInvitationTarget.Home
                            SessionState.Guest, SessionState.Uninitialized -> InvalidInvitationTarget.Login
                        }
                    _uiState.update {
                        it.copy(
                            invitationTuripId = null,
                            invitationTuripName = null,
                            dialogState = InvitationEntryDialogState.Invalid(target = target),
                        )
                    }
                }

                is InvitationEntryResult.Failure -> {
                    val target =
                        when (sessionState.value) {
                            SessionState.Member -> InvalidInvitationTarget.Home
                            SessionState.Guest, SessionState.Uninitialized -> InvalidInvitationTarget.Login
                        }
                    val retryable = result.errorType == ErrorType.Network

                    _uiState.update {
                        it.copy(
                            invitationTuripId = null,
                            invitationTuripName = null,
                            dialogState =
                                InvitationEntryDialogState.Failure(
                                    target = target,
                                    retryable = retryable,
                                ),
                        )
                    }
                }
            }
        }
    }

    fun retryResolveInvitationEntry() {
        _uiState.update { it.copy(dialogState = null) }
        resolveInvitationEntry()
    }

    fun confirmEnterInvitedTurip() {
        val turipId = _uiState.value.invitationTuripId ?: return
        _uiState.update {
            it.copy(
                invitationTuripId = null,
                invitationTuripName = null,
            )
        }

        viewModelScope.launch {
            _uiEffect.send(InvitationEntryUiEffect.NavigateToTuripDetail(turipId))
        }
    }

    fun cancelEnterInvitedTurip() {
        _uiState.update {
            it.copy(
                invitationTuripId = null,
                invitationTuripName = null,
            )
        }

        viewModelScope.launch {
            _uiEffect.send(InvitationEntryUiEffect.NavigateToHome)
        }
    }

    fun resolveFailureDialog() {
        val target =
            (_uiState.value.dialogState as? InvitationEntryDialogState.Failure)?.target ?: return
        _uiState.update { it.copy(dialogState = null) }

        navigateByTarget(target)
    }

    fun resolveInvalidDialog() {
        val target =
            (_uiState.value.dialogState as? InvitationEntryDialogState.Invalid)?.target ?: return
        _uiState.update { it.copy(dialogState = null) }

        navigateByTarget(target)
    }

    private fun navigateByTarget(target: InvalidInvitationTarget) {
        viewModelScope.launch {
            when (target) {
                InvalidInvitationTarget.Home -> _uiEffect.send(InvitationEntryUiEffect.NavigateToHome)
                InvalidInvitationTarget.Login -> _uiEffect.send(InvitationEntryUiEffect.NavigateToLogin)
            }
        }
    }
}
