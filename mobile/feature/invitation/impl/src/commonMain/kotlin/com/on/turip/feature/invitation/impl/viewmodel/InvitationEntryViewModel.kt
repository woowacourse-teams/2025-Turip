package com.on.turip.feature.invitation.impl.viewmodel

import androidx.lifecycle.viewModelScope
import com.on.turip.core.domain.repository.InvitationRepository
import com.on.turip.core.ui.BaseViewModel
import kotlinx.coroutines.launch

class InvitationEntryViewModel(
    private val deepLinkUrl: String,
    private val invitationRepository: InvitationRepository,
) : BaseViewModel<InvitationEntryIntent, InvitationEntryState, InvitationEntryEffect>(
    InvitationEntryState(),
) {
    init {
        fetchInvitationInfo()
    }

    override fun onIntent(intent: InvitationEntryIntent) {
        when (intent) {
            InvitationEntryIntent.ClickJoin -> handleJoin()
            InvitationEntryIntent.ClickBack -> emitEffect(InvitationEntryEffect.NavigateBack)
        }
    }

    private fun fetchInvitationInfo() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            val token = extractTokenFromDeepLink(deepLinkUrl)
            invitationRepository.fetchInvitationInfo(token)
                .onSuccess { info ->
                    updateState {
                        copy(
                            isLoading = false,
                            isFetched = true,
                            invitationInfo = info,
                        )
                    }
                }.onFailure {
                    updateState {
                        copy(
                            isLoading = false,
                            isFetched = true,
                            invitationInfo = null,
                        )
                    }
                    emitEffect(InvitationEntryEffect.ShowSnackbar("초대 정보를 불러오는 데 실패했습니다."))
                }
        }
    }

    private fun handleJoin() {
        val info = currentState.invitationInfo ?: return
        if (currentState.isJoining) return

        if (info.isAlreadyJoined) {
            emitEffect(InvitationEntryEffect.NavigateToHome)
            return
        }

        viewModelScope.launch {
            updateState { copy(isJoining = true) }
            invitationRepository.joinTurip(info.turipId)
                .onSuccess {
                    emitEffect(InvitationEntryEffect.NavigateToHome)
                }.onFailure {
                    updateState { copy(isJoining = false) }
                    emitEffect(InvitationEntryEffect.ShowSnackbar("투립 참여에 실패했습니다. 다시 시도해 주세요."))
                }
        }
    }

    private fun extractTokenFromDeepLink(deepLinkUrl: String): String {
        // Extract the last path segment as the invitation token
        return deepLinkUrl.trimEnd('/').substringAfterLast('/')
    }
}
