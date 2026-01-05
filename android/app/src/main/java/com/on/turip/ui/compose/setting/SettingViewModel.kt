package com.on.turip.ui.compose.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.core.result.ErrorType
import com.on.turip.core.result.onFailure
import com.on.turip.core.result.onSuccess
import com.on.turip.domain.login.MemberRepository
import com.on.turip.domain.setting.InquiryMail
import com.on.turip.domain.setting.PrivacyPolicy
import com.on.turip.domain.userstorage.repository.UserStorageRepository
import com.on.turip.platform.device.AppEnvironmentInfoProvider
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.error.UiError
import com.on.turip.ui.common.error.toUiError
import com.on.turip.ui.compose.setting.model.SettingRetryAction
import com.on.turip.ui.compose.setting.model.SettingUiEffect
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
    private val appEnvironmentInfoProvider: AppEnvironmentInfoProvider,
) : ViewModel() {
    private val _uiState: MutableStateFlow<SettingUiState> = MutableStateFlow(SettingUiState.EMPTY)
    val uiState: StateFlow<SettingUiState> = _uiState

    private val _uiEffect: Channel<SettingUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<SettingUiEffect> = _uiEffect.receiveAsFlow()

    init {
        loadId()
    }

    private fun loadId() {
        viewModelScope.launch {
            userStorageRepository
                .loadId()
                .onSuccess { result ->
                    _uiState.update { it.copy(deviceIdentifier = result) }
                }.onFailure {
                    Timber.e("${it.message}")
                }
        }
    }

    fun loadInquiryMail(): InquiryMail =
        InquiryMail(
            appEnvironmentInfo = appEnvironmentInfoProvider.getAppEnvironmentInfo(),
            fid = uiState.value.deviceIdentifier.fid,
        )

    fun loadPrivacyPolicyLink(): String = PrivacyPolicy.LINK

    fun onLogoutDialogVisibilityChange(visible: Boolean) {
        _uiState.update { it.copy(showLogoutDialog = visible) }
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
                            _uiEffect.send(SettingUiEffect.NavigateToLogin)
                            Timber.d("로그아웃 성공")
                        }.onFailure {
                            Timber.e("토큰 초기화 실패")
                        }
                }.onFailure { errorType: ErrorType ->
                    when (val uiError: UiError = errorType.toUiError()) {
                        is UiError.Global -> handleGlobalError(uiError, SettingRetryAction.LOGOUT)
                        is UiError.Feature -> Unit
                    }
                    Timber.e("로그아웃 실패")
                }
        }
    }

    fun updateWithdrawDialogVisibility(visible: Boolean) {
        _uiState.update { it.copy(showWithdrawDialog = visible) }
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
                            _uiEffect.send(SettingUiEffect.NavigateToLogin)
                            Timber.d("회원탈퇴 성공")
                        }.onFailure {
                            Timber.e("토큰 초기화 실패")
                        }
                }.onFailure { errorType: ErrorType ->
                    when (val uiError: UiError = errorType.toUiError()) {
                        is UiError.Global -> handleGlobalError(uiError, SettingRetryAction.WITHDRAW)
                        is UiError.Feature -> Unit
                    }
                    Timber.e("회원탈퇴 실패 ")
                }
        }
    }

    private suspend fun handleGlobalError(
        uiError: UiError.Global,
        retryAction: SettingRetryAction,
    ) {
        when (uiError) {
            UiError.Global.Network ->
                _uiEffect.send(SettingUiEffect.ShowError(ErrorUiState.Network, retryAction))

            UiError.Global.Server ->
                _uiEffect.send(SettingUiEffect.ShowError(ErrorUiState.Server, retryAction))

            UiError.Global.TokenExpired -> _uiEffect.send(SettingUiEffect.NavigateToLogin)
        }
    }

    fun handleErrorRetryRequest(action: SettingRetryAction) {
        when (action) {
            SettingRetryAction.LOGOUT -> confirmLogout()
            SettingRetryAction.WITHDRAW -> confirmWithdraw()
        }
    }
}
