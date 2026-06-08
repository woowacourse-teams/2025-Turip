package com.on.turip.feature.splash.impl

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.ic_turip_logo_kr
import com.on.turip.core.designsystem.theme.TuripTheme
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SplashScreen(
    deepLinkUrl: String?,
    onNavigateToMain: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToInvitationEntry: (deepLinkUrl: String) -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = koinViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.determineStartDestination(deepLinkUrl)
    }
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collectLatest { uiEffect ->
            when (uiEffect) {
                SplashUiEffect.NavigateToMain -> {
                    onNavigateToMain()
                }

                SplashUiEffect.NavigateToLogin -> {
                    onNavigateToLogin()
                }

                is SplashUiEffect.NavigateToInvitationEntry -> {
                    onNavigateToInvitationEntry(uiEffect.deepLinkUrl)
                }
            }
        }
    }

    SplashScreenContent(modifier)
}

@Composable
private fun SplashScreenContent(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(color = Color(0xFF232323)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_turip_logo_kr),
                contentDescription = null,
                modifier = Modifier.size(120.dp),
            )

            Spacer(modifier = Modifier.height(TuripTheme.spacing.large))

            Text(
                text = "직진만 남은 여행",
                style = TuripTheme.typography.title3,
                color = TuripTheme.colors.white,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    TuripTheme {
        SplashScreenContent()
    }
}
