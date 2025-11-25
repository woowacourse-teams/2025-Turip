package com.on.turip.ui.compose.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.on.turip.R
import com.on.turip.data.login.datasource.GoogleCredentialManager
import com.on.turip.ui.compose.common.component.HelpText
import com.on.turip.ui.compose.common.util.noRippleClickable
import com.on.turip.ui.compose.theme.TuripTypography

@Composable
fun LoginScreen(
    navigateToMain: () -> Unit,
    googleCredentialManager: GoogleCredentialManager,
    viewmodel: LoginViewmodel = hiltViewModel(),
) {
    val uiState: LoginUiState by viewmodel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewmodel.uiEvent.collect { event ->
            when (event) {
                LoginUiEvent.NavigateToMain -> navigateToMain()
            }
        }
    }

    LoginScreen(
        uiState = uiState,
        onChangeHelpTextVisible = { visible: Boolean -> viewmodel.updateHelpTextVisible(visible) },
        onClickGoogleLogin = { viewmodel.onGoogleLogin(googleCredentialManager) },
        onClickGuestLogin = viewmodel::onGuestLogin,
    )
}

@Composable
private fun LoginScreen(
    uiState: LoginUiState,
    onChangeHelpTextVisible: (Boolean) -> Unit,
    onClickGoogleLogin: () -> Unit,
    onClickGuestLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier =
            modifier.noRippleClickable { onChangeHelpTextVisible(false) },
    ) { innerPadding ->
        Image(
            painter = painterResource(R.drawable.bg_login),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        LoginScreenContent(
            isHelpTextVisible = uiState.showHelpText,
            modifier = Modifier.padding(innerPadding),
            onClickHelpText = { onChangeHelpTextVisible(!uiState.showHelpText) },
            onClickGoogleLogin = onClickGoogleLogin,
            onClickGuestLogin = onClickGuestLogin,
        )
    }
}

@Composable
private fun LoginScreenContent(
    isHelpTextVisible: Boolean,
    onClickHelpText: () -> Unit,
    onClickGoogleLogin: () -> Unit,
    onClickGuestLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_logo),
                contentDescription = null,
                modifier =
                    Modifier
                        .padding(top = 32.dp, start = 23.dp)
                        .size(width = 160.dp, height = 61.dp),
            )

            Text(
                text = stringResource(R.string.all_turip_description),
                style = TuripTypography.titleSmall,
                modifier = Modifier.padding(top = 5.dp),
            )
        }

        Column(
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 20.dp, end = 20.dp, bottom = 44.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            if (isHelpTextVisible) {
                Text(
                    text = stringResource(R.string.login_help_description),
                    color = Color.White,
                    style = TuripTypography.bodyLarge,
                    modifier =
                        Modifier
                            .border(
                                border =
                                    BorderStroke(
                                        1.dp,
                                        colorResource(R.color.gray_200_c1c1c1),
                                    ),
                                shape = RoundedCornerShape(10.dp),
                            ).background(
                                color = colorResource(R.color.gray_300_5b5b5b),
                                shape = RoundedCornerShape(10.dp),
                            ).fillMaxWidth()
                            .padding(vertical = 20.dp),
                    textAlign = TextAlign.Center,
                )
            }

            GoogleLoginButton(onClickLoginButton = onClickGoogleLogin)
            HelpText(
                text = stringResource(R.string.login_start_to_guest),
                style = TuripTypography.bodyLarge,
                color = Color.White,
                onClickIcon = onClickHelpText,
                onClickText = onClickGuestLogin,
            )
        }
    }
}

@Composable
@Preview(showBackground = true, name = "HelpVisible")
private fun HelpVisibleLoginScreenPreview() {
    LoginScreenContent(
        isHelpTextVisible = true,
        onClickHelpText = {},
        onClickGoogleLogin = {},
        onClickGuestLogin = {},
    )
}

@Composable
@Preview(showBackground = true, name = "HelpInvisible")
private fun HelpInvisibleLoginScreenPreview() {
    LoginScreenContent(
        isHelpTextVisible = false,
        onClickHelpText = {},
        onClickGoogleLogin = {},
        onClickGuestLogin = {},
    )
}
