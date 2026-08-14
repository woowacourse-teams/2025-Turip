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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class NotificationSettingViewModel(
    private val fcmTokenRepository: FcmTokenRepository,
    private val registerFcmTokenUseCase: RegisterFcmTokenUseCase,
) : BaseViewModel<NotificationSettingIntent, NotificationSettingState, NotificationSettingEffect>(
        NotificationSettingState(),
    ) {
    private var toggleJob: Job? = null

    init {
        loadNotificationEnabled()
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

    private fun toggleNotification(enabled: Boolean) {
        if (enabled && !currentState.isSystemNotificationEnabled) {
            updateState { copy(dialogState = NotificationSettingDialogState.SystemNotificationDisabled) }
        }

        val previousEnabled = currentState.isPushNotificationEnabled
        updateState { copy(isPushNotificationEnabled = enabled) }

        toggleJob?.cancel()
        toggleJob =
            viewModelScope.launch {
                updateNotificationEnabled(enabled = enabled, previousEnabled = previousEnabled)
            }
    }

    private suspend fun updateNotificationEnabled(
        enabled: Boolean,
        previousEnabled: Boolean,
    ) {
        val result = fcmTokenRepository.updateNotificationEnabled(enabled)
        if (result is TuripResult.Success) return

        val errorType = (result as TuripResult.Failure).errorType
        if (errorType !is ErrorType.FcmToken.NotFound) {
            rollbackToggle(previousEnabled)
            return
        }

        if (registerFcmTokenUseCase() !is TuripResult.Success) {
            rollbackToggle(previousEnabled)
            return
        }
        if (fcmTokenRepository.updateNotificationEnabled(enabled) !is TuripResult.Success) {
            rollbackToggle(previousEnabled)
        }
    }

    private fun rollbackToggle(previousEnabled: Boolean) {
        updateState { copy(isPushNotificationEnabled = previousEnabled) }
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
