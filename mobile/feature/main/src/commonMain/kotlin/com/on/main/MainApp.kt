package com.on.turip.feature.main

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.on.turip.core.common.deeplink.DeepLinkEventSource
import com.on.turip.core.common.deeplink.InvitationNavigationCoordinator
import com.on.turip.core.common.deeplink.StartupNavigationArbiter
import com.on.turip.core.designsystem.component.TuripSnackbar
import com.on.turip.core.designsystem.snackbar.LocalSnackbarDelegate
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.navigation.Navigator
import com.on.turip.core.navigation.rememberNavigationState
import com.on.turip.core.navigation.toEntries
import com.on.turip.feature.home.api.HomeNavKey
import com.on.turip.feature.invitation.api.InvitationEntryNavKey
import com.on.turip.feature.login.api.LoginNavKey
import com.on.turip.feature.main.component.ExitConfirmationHandler
import com.on.turip.feature.main.component.LocalSystemBarStyleController
import com.on.turip.feature.main.component.NotificationPermissionEffect
import com.on.turip.feature.main.component.SystemBarStyleEffect
import com.on.turip.feature.main.component.TuripNavigationBar
import com.on.turip.feature.main.navigation.SavedStateConfigurationProvider
import com.on.turip.feature.main.navigation.TopLevel
import com.on.turip.feature.main.navigation.appScreens
import com.on.turip.feature.main.navigation.rememberTuripAppState
import com.on.turip.feature.splash.api.SplashNavKey
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

@Composable
fun MainApp(
    savedStateConfigurationProvider: SavedStateConfigurationProvider,
    deepLinkEventSource: DeepLinkEventSource,
    initialDeepLinkUrl: String? = null,
) {
    val initialEntryKey: NavKey = SplashNavKey(deepLinkUrl = initialDeepLinkUrl)
    val navigationState =
        rememberNavigationState(
            startKey = HomeNavKey,
            topLevelKeys = TopLevel.routes.keys,
            initialEntryKey = initialEntryKey,
            configuration = savedStateConfigurationProvider.savedStateConfiguration,
        )

    val appState = rememberTuripAppState(navigationState = navigationState)
    val navigator = remember(appState.navigationState) { Navigator(appState.navigationState) }

    // Splash 세션 진입과 동일 인스턴스를 공유하는 startup-수명 가드(프로세스 싱글턴).
    // Splash가 프로세스당 1회만 실행되므로 owner 잔존이 일반 네비게이션을 막지 않는다.
    val startupNavigationArbiter: StartupNavigationArbiter = koinInject()
    val invitationNavigationCoordinator: InvitationNavigationCoordinator = koinInject()

    LaunchedEffect(deepLinkEventSource) {
        deepLinkEventSource.events.collect { event ->
            // 원본 URL을 그대로 넘긴다(토큰 재조립 시 이중 디코딩으로 손상되는 문제 방지).
            // 딥링크가 owner를 선점(AlreadyHandling 포함)해 늦은 Splash 세션 네비게이션이 덮지 못하게 한다.
            startupNavigationArbiter.navigateByDeepLink {
                invitationNavigationCoordinator.navigateInvitationIfNeeded(event.dedupeKey) {
                    navigator.goWithAllClear(InvitationEntryNavKey(event.originalUrl))
                }
            }
            deepLinkEventSource.acknowledge(event)
        }
    }

    // 초대 흐름이 top-level 탭으로 빠져나오면 가드를 해제해 동일 링크 재탭을 다시 허용한다.
    LaunchedEffect(appState.currentScreenKey) {
        if (appState.currentScreenKey in TopLevel.routes.keys) {
            invitationNavigationCoordinator.onFlowEnded()
        }
    }

    // shouldShowBottomBar가 true로 바뀔 때 이전 화면이 인증 화면(Splash/Login)인 경우에만
    // 화면 전환 애니메이션(300ms)이 끝난 뒤 바텀 바를 표시해 로그인 화면 위에 겹쳐 보이는 현상을 방지한다.
    // 서브 화면(TuripDetail 등)에서 뒤로 돌아오는 경우에는 즉시 표시한다.
    var prevKey by remember { mutableStateOf<NavKey?>(null) }
    var bottomBarVisible by remember { mutableStateOf(appState.shouldShowBottomBar) }
    LaunchedEffect(appState.shouldShowBottomBar) {
        if (appState.shouldShowBottomBar) {
            val isFromAuthScreen = prevKey is SplashNavKey || prevKey is LoginNavKey
            if (isFromAuthScreen) {
                delay(SCREEN_TRANSITION_DURATION_MS)
            }
        }
        prevKey = appState.currentScreenKey
        bottomBarVisible = appState.shouldShowBottomBar
    }
    val systemBarStyleController = appState.systemBarStyleController

    val animatedSnackbarBottomPadding: Dp by animateDpAsState(
        targetValue = appState.snackbarBottomPadding,
        animationSpec = tween(durationMillis = 220),
    )

    val snackbarHostModifier: Modifier =
        if (bottomBarVisible) {
            Modifier.padding(bottom = animatedSnackbarBottomPadding)
        } else {
            Modifier
                .padding(bottom = animatedSnackbarBottomPadding)
                .navigationBarsPadding()
        }

    val appEntryProvider: (navKey: NavKey) -> NavEntry<NavKey> =
        remember(navigator, savedStateConfigurationProvider.providers) {
            entryProvider {
                appScreens(navigator, savedStateConfigurationProvider.providers)
            }
        }

    val appEntries: List<NavEntry<NavKey>> = appState.navigationState.toEntries(appEntryProvider)

    TuripTheme {
        SystemBarStyleEffect(style = systemBarStyleController.style)
        NotificationPermissionEffect()

        Scaffold(
            containerColor = TuripTheme.colors.background,
            bottomBar = {
                if (bottomBarVisible) {
                    TuripNavigationBar(
                        items = TopLevel.routes,
                        selectedKey = appState.currentScreenKey,
                        onSelectedKeyChange = navigator::navigate,
                    )
                }
            },
            snackbarHost = {
                TuripSnackbar(
                    snackbarHostState = appState.snackbarHostState,
                    modifier = snackbarHostModifier,
                )
            },
            contentWindowInsets = WindowInsets(),
        ) { paddingValues ->
            CompositionLocalProvider(
                LocalSnackbarDelegate provides appState.snackbarDelegate,
                LocalSystemBarStyleController provides appState.systemBarStyleController,
            ) {
                Box(modifier = Modifier.background(TuripTheme.colors.background)) {
                    NavDisplay(
                        entries = appEntries,
                        onBack = navigator::goBack,
                        modifier =
                            Modifier
                                .padding(paddingValues)
                                .consumeWindowInsets(paddingValues)
                                .background(TuripTheme.colors.background),
                        transitionSpec = {
                            fadeTransition()
                        },
                        popTransitionSpec = {
                            fadeTransition()
                        },
                    )
                    ExitConfirmationHandler(appState = appState)
                }
            }
        }
    }
}

private const val SCREEN_TRANSITION_DURATION_MS = 300L

private fun fadeTransition(): ContentTransform =
    ContentTransform(
        targetContentEnter = fadeIn(animationSpec = tween(durationMillis = 300)),
        initialContentExit = fadeOut(animationSpec = tween(durationMillis = 300)),
    )
