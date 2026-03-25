package com.on.turip.ui.main

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.on.turip.core.navigation.InitialNavigationTarget
import com.on.turip.ui.compose.main.MainApp
import com.on.turip.ui.compose.main.navigation.SavedStateConfigurationProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var savedStateConfigurationProvider: SavedStateConfigurationProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val shouldLockPortrait = resources.configuration.smallestScreenWidthDp < 600
        requestedOrientation =
            if (shouldLockPortrait) ActivityInfo.SCREEN_ORIENTATION_PORTRAIT else ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        val initialNavigationTarget = resolveInitialNavigationTarget(savedInstanceState)
        setContent {
            MainApp(
                savedStateConfigurationProvider = savedStateConfigurationProvider,
                initialNavigationTarget = initialNavigationTarget,
            )
        }
    }

    /**
     * 최초 생성일 때만 초기 진입 타겟을 만든다.
     *
     * Activity 재생성 시에는 `savedInstanceState`로 복원된 네비게이션 상태를 우선 사용한다.
     */
    private fun resolveInitialNavigationTarget(savedInstanceState: Bundle?): InitialNavigationTarget? {
        if (savedInstanceState != null) return null

        val targetType: String = intent.getStringExtra(EXTRA_INITIAL_TARGET_TYPE) ?: return InitialNavigationTarget.Login
        val deepLinkUrl: String? = intent.getStringExtra(EXTRA_DEEP_LINK_URL)

        return when (targetType) {
            TARGET_HOME -> {
                InitialNavigationTarget.Home
            }

            TARGET_LOGIN -> {
                InitialNavigationTarget.Login
            }

            TARGET_INVITATION_ENTRY -> {
                deepLinkUrl
                    ?.takeIf(String::isNotBlank)
                    ?.let { url: String -> InitialNavigationTarget.InvitationEntry(deepLinkUrl = url) }
                    ?: InitialNavigationTarget.Login
            }

            else -> {
                InitialNavigationTarget.Login
            }
        }
    }

    companion object {
        private const val EXTRA_INITIAL_TARGET_TYPE = "EXTRA_INITIAL_TARGET_TYPE"
        private const val EXTRA_DEEP_LINK_URL = "EXTRA_DEEP_LINK_URL"
        private const val TARGET_HOME = "TARGET_HOME"
        private const val TARGET_LOGIN = "TARGET_LOGIN"
        private const val TARGET_INVITATION_ENTRY = "TARGET_INVITATION_ENTRY"

        fun newIntent(
            context: Context,
            initialNavigationTarget: InitialNavigationTarget,
        ): Intent =
            Intent(context, MainActivity::class.java).apply {
                when (initialNavigationTarget) {
                    InitialNavigationTarget.Home -> {
                        putExtra(EXTRA_INITIAL_TARGET_TYPE, TARGET_HOME)
                    }

                    InitialNavigationTarget.Login -> {
                        putExtra(EXTRA_INITIAL_TARGET_TYPE, TARGET_LOGIN)
                    }

                    is InitialNavigationTarget.InvitationEntry -> {
                        putExtra(EXTRA_INITIAL_TARGET_TYPE, TARGET_INVITATION_ENTRY)
                        putExtra(EXTRA_DEEP_LINK_URL, initialNavigationTarget.deepLinkUrl)
                    }
                }
            }
    }
}
