package com.on.turip.feature.mypage.impl.notificationsetting

import androidx.lifecycle.viewModelScope
import com.on.turip.core.domain.repository.FcmTokenRepository
import com.on.turip.core.domain.usecase.RegisterFcmTokenUseCase
import com.on.turip.core.model.result.ErrorType
import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.model.result.onFailureWithCause
import com.on.turip.core.model.result.onSuccess
import com.on.turip.core.ui.BaseViewModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

class NotificationSettingViewModel(
    private val fcmTokenRepository: FcmTokenRepository,
    private val registerFcmTokenUseCase: RegisterFcmTokenUseCase,
) : BaseViewModel<NotificationSettingIntent, NotificationSettingState, NotificationSettingEffect>(
        NotificationSettingState(),
    ) {
    /**
     * 진행 중인 요청을 취소해도 이미 서버로 나간 변경은 되돌릴 수 없다.
     * 요청을 순차 처리해 마지막 사용자의 선택이 서버에 마지막으로 반영되도록 직렬화한다.
     * CONFLATED이므로 대기 중 중간 선택은 최신 값으로 덮인다.
     */
    private val toggleRequests: Channel<Boolean> = Channel(Channel.CONFLATED)

    init {
        loadNotificationEnabled()
        consumeToggleRequests()
    }

    override fun onIntent(intent: NotificationSettingIntent) {
        when (intent) {
            is NotificationSettingIntent.ToggleNotification -> toggleNotification(intent.enabled)
            is NotificationSettingIntent.UpdateSystemPermission -> updateSystemPermission(intent.granted)
            NotificationSettingIntent.ClickGoToSettings -> handleClickGoToSettings()
            NotificationSettingIntent.DismissDialog -> dismissDialog()
        }
    }

    private fun loadNotificationEnabled() {
        viewModelScope.launch {
            fcmTokenRepository
                .getNotificationEnabled()
                .onSuccess { enabled ->
                    updateState { copy(isPushNotificationEnabled = enabled, isLoading = false) }
                }.onFailureWithCause { errorType, cause ->
                    Napier.w("알림 설정 조회 실패. errorType=$errorType", cause, tag = "FcmToken")
                    updateState { copy(isLoading = false) }
                    emitEffect(NotificationSettingEffect.ShowLoadFailed)
                }
        }
    }

    private fun consumeToggleRequests() {
        viewModelScope.launch {
            for (enabled in toggleRequests) {
                updateNotificationEnabled(enabled)
            }
        }
    }

    private fun toggleNotification(enabled: Boolean) {
        if (enabled && !currentState.isSystemNotificationEnabled) {
            updateState { copy(dialogState = NotificationSettingDialogState.SystemNotificationDisabled) }
        }

        updateState { copy(isPushNotificationEnabled = enabled) }
        toggleRequests.trySend(enabled)
    }

    private suspend fun updateNotificationEnabled(enabled: Boolean) {
        val result = fcmTokenRepository.updateNotificationEnabled(enabled)
        if (result is TuripResult.Success) return

        val errorType = (result as TuripResult.Failure).errorType
        if (errorType !is ErrorType.FcmToken.NotFound) {
            rollbackToggle(enabled)
            return
        }

        if (registerFcmTokenUseCase() !is TuripResult.Success) {
            rollbackToggle(enabled)
            return
        }
        if (fcmTokenRepository.updateNotificationEnabled(enabled) !is TuripResult.Success) {
            rollbackToggle(enabled)
        }
    }

    /**
     * 실패한 요청 이후 사용자가 다시 토글했다면 그 요청이 상태를 결정한다.
     * 이미 지나간 요청의 실패로 최신 선택을 덮어쓰지 않는다.
     */
    private fun rollbackToggle(failedEnabled: Boolean) {
        if (currentState.isPushNotificationEnabled != failedEnabled) return

        updateState { copy(isPushNotificationEnabled = !failedEnabled) }
        emitEffect(NotificationSettingEffect.ShowUpdateFailed)
    }

    private fun updateSystemPermission(granted: Boolean) {
        updateState { copy(isSystemNotificationEnabled = granted) }
    }

    private fun handleClickGoToSettings() {
        updateState { copy(dialogState = null) }
        emitEffect(NotificationSettingEffect.OpenNotificationSettings)
    }

    private fun dismissDialog() {
        updateState { copy(dialogState = null) }
    }
}
