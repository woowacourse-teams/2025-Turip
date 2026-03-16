package com.on.turip.core.navigation

import androidx.compose.runtime.Immutable

/**
 * 앱 최초 진입 시 사용할 목적지를 표현한다.
 *
 * 딥링크 URL은 InvitationEntry 진입에서만 필요하므로 해당 타입에서만 가진다.
 */
@Immutable
sealed interface InitialNavigationTarget {
    @Immutable
    data object Home : InitialNavigationTarget

    @Immutable
    data object Login : InitialNavigationTarget

    @Immutable
    data class InvitationEntry(
        val deepLinkUrl: String,
    ) : InitialNavigationTarget
}
