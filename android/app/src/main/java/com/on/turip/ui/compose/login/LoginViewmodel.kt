package com.on.turip.ui.compose.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.on.turip.common.AuthState
import com.on.turip.common.UserType
import com.on.turip.data.common.onFailure
import com.on.turip.data.common.onSuccess
import com.on.turip.data.login.datasource.GoogleCredentialManager
import com.on.turip.domain.login.MemberRepository
import com.on.turip.domain.login.usecase.LoginUserUseCase
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
class LoginViewmodel @Inject constructor(
    private val loginUserUseCase: LoginUserUseCase,
    private val memberRepository: MemberRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<LoginUiState> = MutableStateFlow(LoginUiState.EMPTY)
    val uiState: StateFlow<LoginUiState> = _uiState

    private val _uiEvent: Channel<LoginUiEvent> = Channel(Channel.BUFFERED)
    val uiEvent: Flow<LoginUiEvent> = _uiEvent.receiveAsFlow()

    fun updateHelpTextVisible(show: Boolean) {
        _uiState.update { it.copy(showHelpText = show) }
    }

    fun onGoogleLogin(googleCredentialManager: GoogleCredentialManager) {
        viewModelScope.launch {
            googleCredentialManager
                .getIdToken()
                .onSuccess { result: GoogleIdTokenCredential ->
                    loginUserUseCase(result.idToken)
                        .onSuccess { isNewMember: Boolean ->
                            AuthState.change(UserType.MEMBER)
                            if (isNewMember) {
                                showMigrationDialog(true)
                            } else {
                                _uiEvent.send(LoginUiEvent.NavigateToMain)
                            }
                        }.onFailure {
                            Timber.e("Token 저장 실패")
                        }
                }.onFailure {
                    Timber.e("IdToken불러오기 실패")
                }
        }
    }

    private fun showMigrationDialog(show: Boolean) {
        _uiState.update { it.copy(showMigrationDialog = show) }
    }

    fun migration() {
        viewModelScope.launch {
            memberRepository
                .updateMigration()
                .onSuccess {
                    _uiEvent.send(LoginUiEvent.NavigateToMain)
                }.onFailure {
                    Timber.e("마이그레이션 실패")
                }
        }
    }

    fun clearGuestData() {
        viewModelScope.launch {
            memberRepository
                .deleteGuestData()
                .onSuccess {
                    _uiEvent.send(LoginUiEvent.NavigateToMain)
                }.onFailure {
                    Timber.e("게스트 데이터 삭제 실패")
                }
        }
    }

    fun onGuestLogin() {
        viewModelScope.launch {
            AuthState.change(UserType.GUEST)
            _uiEvent.send(LoginUiEvent.NavigateToMain)
        }
    }
}
