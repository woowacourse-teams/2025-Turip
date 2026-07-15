package com.on.turip.feature.mypage.impl.notificationsetting

import com.on.turip.core.ui.UiEffect

sealed interface NotificationSettingEffect : UiEffect {
    data object OpenNotificationSettings : NotificationSettingEffect

    data object ShowUpdateFailed : NotificationSettingEffect
}
