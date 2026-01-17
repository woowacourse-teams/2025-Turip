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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.on.turip.R
import com.on.turip.data.login.datasource.GoogleCredentialManager
import com.on.turip.ui.common.error.toUiModel
import com.on.turip.ui.compose.designsystem.component.TuripDialog
import com.on.turip.ui.compose.designsystem.component.TuripSnackbar
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.login.component.GoogleLoginButton
import com.on.turip.ui.compose.login.component.HelpText
import com.on.turip.ui.compose.login.model.LoginUiEffect
import com.on.turip.ui.compose.login.model.LoginUiState
import com.on.turip.ui.compose.login.util.noRippleClickable

@Composable
fun LoginScreen(
    navigateToMain: () -> Unit,
    googleCredentialManager: GoogleCredentialManager,
    viewmodel: LoginViewmodel = hiltViewModel(),
) {
    val uiState: LoginUiState by viewmodel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewmodel.uiEffect.collect { effect: LoginUiEffect ->
            when (effect) {
                LoginUiEffect.NavigateToMain -> {
                    navigateToMain()
                }

                is LoginUiEffect.ShowError -> {
                    val errorUiModel = effect.errorUiState.toUiModel() ?: return@collect
                    snackbarHostState.showSnackbar(
                        message = context.getString(errorUiModel.titleRes),
                        duration = SnackbarDuration.Short,
                    )
                }
            }
        }
    }

    if (uiState.showMigrationDialog) {
        TuripDialog(
            title = stringResource(R.string.login_dialog_migration_title),
            message = stringResource(R.string.login_dialog_migration_message),
            confirmText = stringResource(R.string.login_dialog_confirm_text),
            dismissText = stringResource(R.string.setting_logout_dialog_dismiss),
            confirmButtonColor = TuripTheme.colors.primary,
            dismissButtonColor = TuripTheme.colors.gray02,
            onConfirmation = viewmodel::migration,
            onDismissRequest = viewmodel::clearGuestData,
        )
    }

    Scaffold(
        modifier =
            Modifier.noRippleClickable { viewmodel.updateHelpTextVisible(false) },
        snackbarHost = { TuripSnackbar(snackbarHostState = snackbarHostState) },
    ) { innerPadding ->
        LoginScreenContent(
            isHelpTextVisible = uiState.showHelpText,
            modifier = Modifier.padding(innerPadding),
            onClickHelpText = { viewmodel.updateHelpTextVisible(!uiState.showHelpText) },
            onClickGoogleLogin = { viewmodel.onGoogleLogin(googleCredentialManager) },
            onClickGuestLogin = viewmodel::onGuestLogin,
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
    Image(
        painter = painterResource(R.drawable.bg_login),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
    )

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .padding(
                    vertical = TuripTheme.spacing.extraHuge,
                    horizontal = TuripTheme.spacing.extraLarge,
                ),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_logo),
                contentDescription = null,
                modifier = Modifier.size(width = 160.dp, height = 60.dp),
            )

            Text(
                text = stringResource(R.string.all_turip_description),
                style = TuripTheme.typography.title3,
                modifier =
                    Modifier.padding(top = TuripTheme.spacing.extraSmall),
            )
        }

        Column(
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = TuripTheme.spacing.small),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.large),
        ) {
            if (isHelpTextVisible) {
                Text(
                    text = stringResource(R.string.login_help_description),
                    color = TuripTheme.colors.white,
                    style = TuripTheme.typography.body1,
                    modifier =
                        Modifier
                            .border(
                                border = BorderStroke(1.dp, TuripTheme.colors.gray02),
                                shape = TuripTheme.shape.container,
                            ).background(
                                color = TuripTheme.colors.gray03,
                                shape = TuripTheme.shape.container,
                            ).fillMaxWidth()
                            .padding(vertical = TuripTheme.spacing.extraLarge),
                    textAlign = TextAlign.Center,
                )
            }

            GoogleLoginButton(onClickLoginButton = onClickGoogleLogin)

            HelpText(
                text = stringResource(R.string.login_start_to_guest),
                style = TuripTheme.typography.body1,
                color = TuripTheme.colors.white,
                onClickIcon = onClickHelpText,
                onClickText = onClickGuestLogin,
            )
        }
    }
}

@Composable
@Preview(showBackground = true, name = "HelpVisible")
private fun HelpVisibleLoginScreenPreview() {
    TuripTheme {
        LoginScreenContent(
            isHelpTextVisible = true,
            onClickHelpText = { },
            onClickGoogleLogin = { },
            onClickGuestLogin = { },
        )
    }
}

@Composable
@Preview(showBackground = true, name = "HelpInvisible")
private fun HelpInvisibleLoginScreenPreview() {
    TuripTheme {
        LoginScreenContent(
            isHelpTextVisible = false,
            onClickHelpText = { },
            onClickGoogleLogin = { },
            onClickGuestLogin = { },
        )
    }
}
