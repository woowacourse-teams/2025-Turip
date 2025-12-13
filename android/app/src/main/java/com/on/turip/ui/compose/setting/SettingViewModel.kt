package com.on.turip.ui.compose.setting

import android.net.Uri
import android.os.Build
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.BuildConfig
import com.on.turip.data.common.onFailure
import com.on.turip.data.common.onSuccess
import com.on.turip.domain.ErrorEvent
import com.on.turip.domain.login.MemberRepository
import com.on.turip.domain.setting.InquiryMail
import com.on.turip.domain.setting.PrivacyPolicy
import com.on.turip.domain.userstorage.repository.UserStorageRepository
import com.on.turip.ui.compose.setting.model.SettingUiEvent
import com.on.turip.ui.compose.setting.model.SettingUiState
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
    private val memberRepository: MemberRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<SettingUiState> = MutableStateFlow(SettingUiState.EMPTY)
    val uiState: StateFlow<SettingUiState> = _uiState

    private val _uiEvent: Channel<SettingUiEvent> = Channel(Channel.BUFFERED)
    val uiEvent: Flow<SettingUiEvent> = _uiEvent.receiveAsFlow()

    init {
        loadId()
    }

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

    fun loadInquiryUri(): Uri {
        val inquiry: InquiryMail =
            InquiryMail(
                appVersionName = BuildConfig.VERSION_NAME,
                appVersionCode = BuildConfig.VERSION_CODE,
                deviceReleaseVersion = Build.VERSION.RELEASE,
                deviceSdkVersion = Build.VERSION.SDK_INT,
                deviceManufacturer = Build.MANUFACTURER,
                deviceModel = Build.MODEL,
                fid = uiState.value.deviceIdentifier.fid,
            )

        return "mailto:${InquiryMail.RECIPIENT}?subject=${Uri.encode(InquiryMail.TITLE)}&body=${
            Uri.encode(inquiry.content)
        }".toUri()
    }

    fun loadPrivacyPolicyLink(): String = PrivacyPolicy.LINK

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
                    _uiEvent.send(SettingUiEvent.ShowError(error))
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
                .deleteMember()
                .onSuccess {
                    userStorageRepository
                        .clearTokens()
                        .onSuccess {
                            _uiEvent.send(SettingUiEvent.Withdraw)
                            Timber.d("회원탈퇴 성공")
                        }.onFailure {
                            Timber.e("토큰 초기화 실패")
                        }
                }.onFailure { error: ErrorEvent ->
                    _uiEvent.send(SettingUiEvent.ShowError(error))
                    Timber.e("회원탈퇴 실패 : $error")
                }
        }
    }
}
