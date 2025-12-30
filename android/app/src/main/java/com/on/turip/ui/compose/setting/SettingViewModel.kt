package com.on.turip.ui.compose.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.data.common.ErrorType
import com.on.turip.data.common.ErrorUiEffect
import com.on.turip.data.common.ErrorUiState
import com.on.turip.data.common.UiError
import com.on.turip.data.common.onFailure
import com.on.turip.data.common.onFailureWithCause
import com.on.turip.data.common.onSuccess
import com.on.turip.data.common.toUiError
import com.on.turip.domain.login.MemberRepository
import com.on.turip.domain.setting.InquiryMail
import com.on.turip.domain.setting.PrivacyPolicy
import com.on.turip.domain.userstorage.repository.UserStorageRepository
import com.on.turip.platform.device.AppEnvironmentInfoProvider
import com.on.turip.ui.common.event.CommonUiEffect
import com.on.turip.ui.compose.setting.model.SettingUiState
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
    private val appEnvironmentInfoProvider: AppEnvironmentInfoProvider,
) : ViewModel() {
    private val _uiState: MutableStateFlow<SettingUiState> = MutableStateFlow(SettingUiState.EMPTY)
    val uiState: StateFlow<SettingUiState> = _uiState

    private val _commonUiEffect: Channel<CommonUiEffect> = Channel(Channel.BUFFERED)
    val commonUiEffect: Flow<CommonUiEffect> = _commonUiEffect.receiveAsFlow()

    private val _errorUiEffect: Channel<ErrorUiEffect> = Channel(Channel.BUFFERED)
    val errorUiEffect: Flow<ErrorUiEffect> = _errorUiEffect.receiveAsFlow()

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

    fun loadInquiryMail(): InquiryMail =
        InquiryMail(
            appEnvironmentInfo = appEnvironmentInfoProvider.getAppEnvironmentInfo(),
            fid = uiState.value.deviceIdentifier.fid,
        )

    fun loadPrivacyPolicyLink(): String = PrivacyPolicy.LINK

    fun onLogoutDialogVisibilityChange(visible: Boolean) {
        _uiState.update { it.copy(showLogoutDialog = visible) }
    }

    fun onLogoutConfirm() {
        viewModelScope.launch {
            _uiState.update { it.copy(showLogoutDialog = false) }

            memberRepository
                .logout()
                .onSuccess {
                    userStorageRepository
                        .clearTokens()
                        .onSuccess {
                            _commonUiEffect.send(CommonUiEffect.NavigateToLogin)
                            Timber.d("로그아웃 성공")
                        }.onFailure {
                            Timber.e("토큰 초기화 실패")
                        }
                }.onFailure { errorType: ErrorType ->
                    when (val uiError: UiError = errorType.toUiError()) {
                        is UiError.Global -> handleGlobalError(uiError)
                        is UiError.Feature -> Unit
                    }
                }.onFailureWithCause { errorType: ErrorType, cause: Throwable? ->
                    Timber.e("로그아웃 실패 : $errorType / $cause")
                }
        }
    }

    fun onWithdrawDialogVisibilityChange(visible: Boolean) {
        _uiState.update { it.copy(showWithdrawDialog = visible) }
    }

    fun onWithdrawConfirm() {
        viewModelScope.launch {
            _uiState.update { it.copy(showWithdrawDialog = false) }

            memberRepository
                .deleteMember()
                .onSuccess {
                    userStorageRepository
                        .clearTokens()
                        .onSuccess {
                            _commonUiEffect.send(CommonUiEffect.NavigateToLogin)
                            Timber.d("회원탈퇴 성공")
                        }.onFailure {
                            Timber.e("토큰 초기화 실패")
                        }
                }.onFailure { errorType: ErrorType ->
                    when (val uiError: UiError = errorType.toUiError()) {
                        is UiError.Global -> handleGlobalError(uiError)
                        is UiError.Feature -> Unit
                    }
                }.onFailureWithCause { errorType: ErrorType, cause: Throwable? ->
                    Timber.e("회원탈퇴 실패 : $errorType / $cause")
                }
        }
    }

    private suspend fun handleGlobalError(uiError: UiError.Global) {
        when (uiError) {
            UiError.Global.Network -> _errorUiEffect.send(ErrorUiEffect.ShowSnackbar(ErrorUiState.Network))
            UiError.Global.Server -> _errorUiEffect.send(ErrorUiEffect.ShowSnackbar(ErrorUiState.Server))
            UiError.Global.TokenExpired -> _commonUiEffect.send(CommonUiEffect.NavigateToLogin)
        }
    }
}
