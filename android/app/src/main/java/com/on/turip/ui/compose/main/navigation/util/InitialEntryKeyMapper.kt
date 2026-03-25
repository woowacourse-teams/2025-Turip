package com.on.turip.ui.compose.main.navigation.util

import androidx.navigation3.runtime.NavKey
import com.on.turip.core.navigation.InitialNavigationTarget
import com.on.turip.ui.compose.home.navigation.HomeNavKey
import com.on.turip.ui.compose.invitation.navigation.InvitationEntryNavKey
import com.on.turip.ui.compose.login.navigation.LoginNavKey

fun InitialNavigationTarget.toInitialEntryKey(): NavKey? =
    when (this) {
        InitialNavigationTarget.Home -> {
            HomeNavKey
        }

        InitialNavigationTarget.Login -> {
            LoginNavKey()
        }

        is InitialNavigationTarget.InvitationEntry -> {
            InvitationEntryNavKey(deepLinkUrl = deepLinkUrl)
        }
    }
