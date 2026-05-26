package com.on.turip.feature.splash.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.feature.splash.impl.viewmodel.SplashEffect
import com.on.turip.feature.splash.impl.viewmodel.SplashViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SplashScreen(
    deepLinkUrl: String?,
    onNavigateToMain: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToInvitationEntry: (deepLinkUrl: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = koinViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.determineStartDestination(deepLinkUrl)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                SplashEffect.NavigateToMain -> onNavigateToMain()
                SplashEffect.NavigateToLogin -> onNavigateToLogin()
                is SplashEffect.NavigateToInvitationEntry -> onNavigateToInvitationEntry(effect.deepLinkUrl)
            }
        }
    }

    SplashContent(modifier)
}

@Composable
private fun SplashContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TuripTheme.colors.primary),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Turip",
            style = TuripTheme.typography.title3,
            color = TuripTheme.colors.white,
        )
    }
}
