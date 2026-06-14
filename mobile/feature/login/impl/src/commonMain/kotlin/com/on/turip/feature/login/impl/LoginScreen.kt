package com.on.turip.feature.login.impl

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.common.Platform
import com.on.turip.core.common.currentPlatform
import com.on.turip.core.designsystem.component.TuripDialog
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.all_close_description
import com.on.turip.core.designsystem.generated.resources.all_turip_description
import com.on.turip.core.designsystem.generated.resources.bg_login
import com.on.turip.core.designsystem.generated.resources.ic_logo
import com.on.turip.core.designsystem.generated.resources.login_dialog_confirm_text
import com.on.turip.core.designsystem.generated.resources.login_dialog_migration_message
import com.on.turip.core.designsystem.generated.resources.login_dialog_migration_title
import com.on.turip.core.designsystem.generated.resources.login_help_description
import com.on.turip.core.designsystem.generated.resources.login_start_to_guest
import com.on.turip.core.designsystem.generated.resources.my_page_logout_dialog_dismiss
import com.on.turip.core.designsystem.snackbar.LocalSnackbarDelegate
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.ui.error.toUiModel
import com.on.turip.feature.login.impl.component.GoogleLoginButton
import com.on.turip.feature.login.impl.component.GuestModeSection
import com.on.turip.feature.login.impl.component.PlatformLoginButton
import com.on.turip.feature.login.impl.util.noRippleClickable
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    deepLinkUrl: String?,
    onNavigateToMain: (deepLinkUrl: String?) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = koinViewModel(),
) {
    val uiState: LoginUiState by viewModel.uiState.collectAsState()
    val googleCredentialManager = rememberGoogleCredentialManager()
    val appleCredentialManager = rememberAppleCredentialManager()
    val snackbarDelegate = LocalSnackbarDelegate.current
    val showAppleLoginButton = currentPlatform == Platform.IOS

    LaunchedEffect(deepLinkUrl) {
        viewModel.initDeepLinkUrl(deepLinkUrl)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collectLatest { effect: LoginUiEffect ->
            when (effect) {
                LoginUiEffect.RequestAutoLogin -> {
                    viewModel.loginWithGoogle(googleCredentialManager)
                }

                is LoginUiEffect.NavigateToMain -> {
                    onNavigateToMain(effect.deepLinkUrl)
                }

                is LoginUiEffect.ShowError -> {
                    val errorUiModel = effect.errorUiState.toUiModel() ?: return@collectLatest
                    snackbarDelegate.showSnackbar(
                        message = getString(errorUiModel.titleRes),
                        actionLabel = getString(Res.string.all_close_description),
                        duration = SnackbarDuration.Short,
                    )
                }
            }
        }
    }

    if (uiState.showMigrationDialog) {
        TuripDialog(
            title = stringResource(Res.string.login_dialog_migration_title),
            message = stringResource(Res.string.login_dialog_migration_message),
            confirmText = stringResource(Res.string.login_dialog_confirm_text),
            dismissText = stringResource(Res.string.my_page_logout_dialog_dismiss),
            onConfirmation = viewModel::confirmMigration,
            onDismissRequest = viewModel::rejectMigration,
        )
    }

    LoginScreenContent(
        isHelpTextVisible = uiState.showHelpText,
        modifier =
            modifier
                .fillMaxSize()
                .noRippleClickable { viewModel.updateHelpTextVisible(false) },
        showAppleLoginButton = showAppleLoginButton,
        onHelpClick = { viewModel.updateHelpTextVisible(!uiState.showHelpText) },
        onGoogleLoginClick = { viewModel.loginWithGoogle(googleCredentialManager) },
        onAppleLoginClick = { viewModel.loginWithApple(appleCredentialManager) },
        onGuestLoginClick = viewModel::continueAsGuest,
    )
}

@Composable
private fun LoginScreenContent(
    isHelpTextVisible: Boolean,
    showAppleLoginButton: Boolean,
    onHelpClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onAppleLoginClick: () -> Unit,
    onGuestLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(Res.drawable.bg_login),
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
                ).statusBarsPadding()
                .navigationBarsPadding(),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_logo),
                contentDescription = null,
                modifier = Modifier.size(width = 160.dp, height = 60.dp),
            )

            Text(
                text = stringResource(Res.string.all_turip_description),
                style = TuripTheme.typography.title3,
                modifier = Modifier.padding(top = TuripTheme.spacing.extraSmall),
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
                    text = stringResource(Res.string.login_help_description),
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

            GoogleLoginButton(
                onLoginClick = onGoogleLoginClick,
                modifier = Modifier.fillMaxWidth(),
            )

            if (showAppleLoginButton) {
                PlatformLoginButton(
                    onLoginClick = onAppleLoginClick,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            GuestModeSection(
                text = stringResource(Res.string.login_start_to_guest),
                color = TuripTheme.colors.white,
                onHelpClick = onHelpClick,
                onTextClick = onGuestLoginClick,
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
            showAppleLoginButton = false,
            onHelpClick = {},
            onGoogleLoginClick = {},
            onAppleLoginClick = {},
            onGuestLoginClick = {},
        )
    }
}

@Composable
@Preview(showBackground = true, name = "HelpInvisible")
private fun HelpInvisibleLoginScreenPreview() {
    TuripTheme {
        LoginScreenContent(
            isHelpTextVisible = false,
            showAppleLoginButton = false,
            onHelpClick = {},
            onGoogleLoginClick = {},
            onAppleLoginClick = {},
            onGuestLoginClick = {},
        )
    }
}
