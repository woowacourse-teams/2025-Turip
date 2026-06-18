package com.on.turip.feature.invitation.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.core.data.session.SessionManager
import com.on.turip.core.domain.repository.TuripRepository
import com.on.turip.core.domain.session.SessionState
import com.on.turip.core.domain.usecase.DetermineInvitationEntryRouteUseCase
import com.on.turip.core.model.invitation.InvitationEntryResult
import com.on.turip.core.model.invitation.InvitationTokenParser
import com.on.turip.core.model.result.ErrorType
import com.on.turip.core.model.result.onFailure
import com.on.turip.core.model.result.onSuccess
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InvitationEntryViewModel(
    private val determineInvitationEntryRouteUseCase: DetermineInvitationEntryRouteUseCase,
    private val turipRepository: TuripRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {
    private val _uiState: MutableStateFlow<InvitationEntryUiState> =
        MutableStateFlow(InvitationEntryUiState.Idle)
    val uiState: StateFlow<InvitationEntryUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<InvitationEntryUiEffect>(Channel.BUFFERED)
    val uiEffect: Flow<InvitationEntryUiEffect> = _uiEffect.receiveAsFlow()

    private val sessionState: StateFlow<SessionState> = sessionManager.state

    fun initDeepLinkUrl(deepLinkUrl: String?) {
        _uiState.update { it.copy(deepLinkUrl = deepLinkUrl?.trim()?.takeIf(String::isNotEmpty)) }
    }

    fun resolveInvitationEntry() {
        viewModelScope.launch {
            val invitationToken: String? =
                InvitationTokenParser.extractTokenFromUrl(_uiState.value.deepLinkUrl)
            when (
                val result: InvitationEntryResult =
                    determineInvitationEntryRouteUseCase(sessionState.value, invitationToken)
            ) {
                is InvitationEntryResult.MemberValidated -> {
                    if (result.invitationInformation.alreadyJoined) {
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
                    val target = invalidInvitationTarget()
                    _uiState.update {
                        it.copy(
                            invitationTuripId = null,
                            invitationTuripName = null,
                            dialogState = InvitationEntryDialogState.Invalid(target = target),
                        )
                    }
                }

                is InvitationEntryResult.Failure -> {
                    if (result.errorType is ErrorType.Auth) {
                        sessionManager.switchToGuest()
                        _uiEffect.send(InvitationEntryUiEffect.NavigateToLogin)
                        return@launch
                    }

                    _uiState.update {
                        it.copy(
                            invitationTuripId = null,
                            invitationTuripName = null,
                            dialogState =
                                InvitationEntryDialogState.Failure(
                                    target = invalidInvitationTarget(),
                                    retryable = result.errorType == ErrorType.Network,
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
            turipRepository
                .joinTurip(turipId)
                .onSuccess {
                    _uiEffect.send(InvitationEntryUiEffect.NavigateToTuripDetail(turipId))
                }.onFailure { errorType ->
                    if (errorType is ErrorType.Auth) {
                        sessionManager.switchToGuest()
                        _uiEffect.send(InvitationEntryUiEffect.NavigateToLogin)
                        return@onFailure
                    }

                    _uiState.update {
                        it.copy(
                            dialogState =
                                InvitationEntryDialogState.Failure(
                                    target = invalidInvitationTarget(),
                                    retryable = errorType == ErrorType.Network,
                                ),
                        )
                    }
                }
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

    private fun invalidInvitationTarget(): InvalidInvitationTarget =
        when (sessionState.value) {
            SessionState.Member -> InvalidInvitationTarget.Home

            SessionState.Guest,
            SessionState.Uninitialized,
            -> InvalidInvitationTarget.Login
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
