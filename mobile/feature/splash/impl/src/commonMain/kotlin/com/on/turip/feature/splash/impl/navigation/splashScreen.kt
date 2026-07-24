package com.on.turip.feature.splash.impl.navigation

import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.core.common.deeplink.StartupNavigationArbiter
import com.on.turip.core.navigation.Navigator
import com.on.turip.feature.home.api.HomeNavKey
import com.on.turip.feature.invitation.api.InvitationEntryNavKey
import com.on.turip.feature.login.api.LoginNavKey
import com.on.turip.feature.splash.api.SplashNavKey
import com.on.turip.feature.splash.impl.SplashScreen
import kotlinx.coroutines.launch

fun EntryProviderScope<NavKey>.splashScreen(
    navigator: Navigator,
    startupNavigationArbiter: StartupNavigationArbiter,
) {
    entry<SplashNavKey> { key ->
        val scope = rememberCoroutineScope()
        SplashScreen(
            deepLinkUrl = key.deepLinkUrl,
            // 세션 기반 진입은 Arbiter를 통해 수행 → 딥링크가 먼저 선점했다면 덮어쓰지 않는다.
            onNavigateToMain = {
                scope.launch {
                    startupNavigationArbiter.navigateBySession { navigator.goWithAllClear(HomeNavKey) }
                }
            },
            onNavigateToLogin = {
                scope.launch {
                    startupNavigationArbiter.navigateBySession {
                        navigator.goWithAllClear(LoginNavKey(key.deepLinkUrl))
                    }
                }
            },
            // 딥링크(Android 콜드/디퍼드) 진입은 DeepLink owner를 선점한다.
            onNavigateToInvitationEntry = { deepLinkUrl ->
                scope.launch {
                    startupNavigationArbiter.navigateByDeepLink {
                        navigator.goWithAllClear(InvitationEntryNavKey(deepLinkUrl))
                    }
                }
            },
        )
    }
}
