package com.on.turip.ui.main.favorite

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.data.common.onFailure
import com.on.turip.data.common.onSuccess
import com.on.turip.domain.ErrorEvent
import com.on.turip.domain.login.MemberRepository
import com.on.turip.domain.userstorage.repository.UserStorageRepository
import com.on.turip.ui.compose.common.util.SettingUtils
import com.on.turip.ui.compose.common.util.SettingUtils.EMAIL_RECIPIENT
import com.on.turip.ui.compose.common.util.SettingUtils.EMAIL_SUBJECT
import com.on.turip.ui.compose.setting.SettingUiEvent
import com.on.turip.ui.compose.setting.SettingUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val userStorageRepository: UserStorageRepository,
    private val memberRepository: MemberRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<SettingUiState> = MutableStateFlow(SettingUiState.EMPTY)
    val uiState: StateFlow<SettingUiState> = _uiState

    private val _uiEvent: Channel<SettingUiEvent> = Channel(Channel.BUFFERED)
    val uiEvent: Flow<SettingUiEvent> = _uiEvent.receiveAsFlow()

    init {
        loadId()
    }

    fun loadId() {
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

            memberRepository
                .logout()
                .onSuccess {
                    userStorageRepository
                        .clearTokens()
                        .onSuccess {
                            _uiEvent.send(SettingUiEvent.Logout)
                            Timber.d("로그아웃 성공")
                        }.onFailure {
                            Timber.e("토큰 초기화 실패")
                        }
                }.onFailure { error: ErrorEvent ->
                    Timber.e("로그아웃 실패 : $error")
                }
        }
    }

    fun showWithdrawDialog(show: Boolean) {
        _uiState.update { it.copy(showWithdrawDialog = show) }
    }

    fun confirmWithdraw() {
        viewModelScope.launch {
            _uiState.update { it.copy(showWithdrawDialog = false) }

            memberRepository
                .withdraw()
                .onSuccess {
                    userStorageRepository
                        .clearTokens()
                        .onSuccess {
                            _uiEvent.send(SettingUiEvent.Logout)
                            Timber.d("로그아웃 성공")
                        }.onFailure {
                            Timber.e("토큰 초기화 실패")
                        }
                }.onFailure { error: ErrorEvent ->
                    Timber.e("로그아웃 실패 : $error")
                }
        }
    }
}
