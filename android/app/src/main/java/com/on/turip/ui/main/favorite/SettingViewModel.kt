package com.on.turip.ui.main.favorite

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.domain.userstorage.repository.UserStorageRepository
import com.on.turip.ui.compose.common.util.SettingUtils
import com.on.turip.ui.compose.common.util.SettingUtils.EMAIL_RECIPIENT
import com.on.turip.ui.compose.common.util.SettingUtils.EMAIL_SUBJECT
import com.on.turip.ui.compose.setting.SettingUiEvent
import com.on.turip.ui.compose.setting.SettingUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val userStorageRepository: UserStorageRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<SettingUiState> = MutableStateFlow(SettingUiState.EMPTY)
    val uiState: StateFlow<SettingUiState> = _uiState

    private val _uiEvent: Channel<SettingUiEvent> = Channel(Channel.BUFFERED)
    val uiEvent: Flow<SettingUiEvent> = _uiEvent.receiveAsFlow()

    init {
        loadId()
    }

    // TODO : 멤버, 게스트 판별 로직 구현 필요 uiState.value.deviceIdentifier.memberStatus
    private fun loadId() {
        viewModelScope.launch {
            userStorageRepository
                .loadId()
                .onSuccess { result ->
                    _uiState.update {
                        uiState.value.copy(deviceIdentifier = result)
                    }
                }.onFailure {
                    Timber.e("${it.message}")
                }
        }
    }

    fun loadInquiryUri(): Uri =
        "mailto:$EMAIL_RECIPIENT?subject=${Uri.encode(EMAIL_SUBJECT)}&body=${
            Uri.encode(SettingUtils.toEmailBody(uiState.value.deviceIdentifier.fid))
        }".toUri()

    fun loadPrivacyPolicyUri(): Uri = SettingUtils.PRIVACY_POLICY_LINK.toUri()

    fun showLogoutDialog(show: Boolean) {
        _uiState.update { it.copy(showLogoutDialog = show) }
    }

    fun confirmLogout() {
        viewModelScope.launch {
            _uiState.update { it.copy(showLogoutDialog = false) }

            // TODO : 로그아웃 처리

            _uiEvent.send(SettingUiEvent.Logout)
        }
    }

    fun showWithdrawDialog(show: Boolean) {
        _uiState.update { it.copy(showWithdrawDialog = show) }
    }

    fun confirmWithdraw() {
        viewModelScope.launch {
            _uiState.update { it.copy(showWithdrawDialog = false) }

            // TODO : 회원탈퇴 처리

            _uiEvent.send(SettingUiEvent.Withdraw)
        }
    }
}
