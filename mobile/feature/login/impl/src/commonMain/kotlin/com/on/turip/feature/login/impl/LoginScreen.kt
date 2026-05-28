package com.on.turip.feature.login.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import com.on.turip.core.designsystem.snackbar.LocalSnackbarDelegate
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.feature.login.impl.component.GoogleLoginButton
import com.on.turip.feature.login.impl.component.GuestModeSection
import com.on.turip.feature.login.impl.viewmodel.LoginEffect
import com.on.turip.feature.login.impl.viewmodel.LoginIntent
import com.on.turip.feature.login.impl.viewmodel.LoginState
import com.on.turip.feature.login.impl.viewmodel.LoginViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun LoginScreen(
    deepLinkUrl: String?,
    onNavigateToHome: () -> Unit,
    onNavigateToInvitationEntry: (deepLinkUrl: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = koinViewModel(parameters = { parametersOf(deepLinkUrl) }),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarDelegate = LocalSnackbarDelegate.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                LoginEffect.NavigateToHome -> onNavigateToHome()
                is LoginEffect.NavigateToInvitationEntry -> onNavigateToInvitationEntry(effect.deepLinkUrl)
                is LoginEffect.ShowSnackbar -> snackbarDelegate.showSnackbar(effect.message)
            }
        }
    }

    LoginContent(
        state = uiState,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}

@Composable
private fun LoginContent(
    state: LoginState,
    onIntent: (LoginIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TuripTheme.colors.background)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Turip",
            style = TuripTheme.typography.display,
            color = TuripTheme.colors.primary,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "여행 친구들과 함께하는 여행 플래너",
            style = TuripTheme.typography.body2,
            color = TuripTheme.colors.gray03,
        )

        Spacer(modifier = Modifier.weight(1f))

        GoogleLoginButton(
            isLoading = state.isLoading,
            onClick = {
                // TODO: Platform-specific Google Sign-In
                // Trigger the platform Google Sign-In flow here, then pass the resulting idToken:
                // onIntent(LoginIntent.ClickGoogleLogin(idToken))
            },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        GuestModeSection(
            onClickGuestLogin = { onIntent(LoginIntent.ClickGuestLogin) },
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}
