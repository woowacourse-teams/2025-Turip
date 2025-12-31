package com.on.turip.ui.compose.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.on.turip.R
import com.on.turip.common.AuthState
import com.on.turip.common.UserType
import com.on.turip.data.common.toUiModel
import com.on.turip.domain.setting.InquiryMail
import com.on.turip.ui.compose.designsystem.component.TuripAppBar
import com.on.turip.ui.compose.designsystem.component.TuripDialog
import com.on.turip.ui.compose.designsystem.component.TuripSnackbar
import com.on.turip.ui.compose.setting.component.SettingItem
import com.on.turip.ui.compose.setting.model.SettingUiEffect
import com.on.turip.ui.compose.setting.model.SettingUiState
import com.on.turip.ui.compose.theme.TuripTheme
import timber.log.Timber

@Composable
fun SettingScreen(
    navigateToBack: () -> Unit,
    navigateToInquiry: (InquiryMail) -> Unit,
    navigateToPrivacyPolicy: (String) -> Unit,
    navigateToLoginScreen: () -> Unit,
    viewModel: SettingViewModel = hiltViewModel(),
) {
    val uiState: SettingUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { uiEffect: SettingUiEffect ->
            when (uiEffect) {
                SettingUiEffect.NavigateToLogin -> navigateToLoginScreen()

                is SettingUiEffect.ShowError -> {
                    val errorUiModel = uiEffect.errorUiState.toUiModel() ?: return@collect
                    snackbarHostState.showSnackbar(context.getString(errorUiModel.titleRes))
                }
            }
        }
    }

    if (uiState.showLogoutDialog) {
        TuripDialog(
            title = stringResource(R.string.setting_logout),
            message = stringResource(R.string.setting_logout_dialog_message),
            confirmText = stringResource(R.string.setting_logout_dialog_confirm),
            dismissText = stringResource(R.string.setting_logout_dialog_dismiss),
            confirmButtonColor = colorResource(R.color.turip_blue_11aebf_70),
            dismissButtonColor = colorResource(R.color.turip_gray_b4b4b4),
            onConfirmation = viewModel::onLogoutConfirm,
            onDismissRequest = { viewModel.onLogoutDialogVisibilityChange(visible = false) },
        )
    }

    if (uiState.showWithdrawDialog) {
        TuripDialog(
            title = stringResource(R.string.setting_withdraw),
            message = stringResource(R.string.setting_withdraw_dialog_message),
            confirmText = stringResource(R.string.setting_withdraw_dialog_confirm),
            dismissText = stringResource(R.string.setting_withdraw_dialog_dismiss),
            confirmButtonColor = colorResource(R.color.turip_red_ff7474),
            dismissButtonColor = colorResource(R.color.turip_gray_b4b4b4),
            onConfirmation = viewModel::onWithdrawConfirm,
            onDismissRequest = { viewModel.onWithdrawDialogVisibilityChange(visible = false) },
        )
    }

    SettingScreen(
        snackbarHostState = snackbarHostState,
        onClickBack = navigateToBack,
        onClickInquiry = {
            navigateToInquiry(viewModel.loadInquiryMail())
            Timber.d("SettingScreen 문의하기 버튼 클릭")
        },
        onClickPrivacyPolicy = {
            navigateToPrivacyPolicy(viewModel.loadPrivacyPolicyLink())
            Timber.d("SettingScreen 개인정보처리 방침 버튼 클릭")
        },
        onClickLogin = {
            navigateToLoginScreen()
            Timber.d("SettingScreen 로그인 버튼 클릭")
        },
        onClickLogout = {
            viewModel.onLogoutDialogVisibilityChange(visible = true)
            Timber.d("SettingScreen 로그아웃 버튼 클릭")
        },
        onClickWithdraw = {
            viewModel.onWithdrawDialogVisibilityChange(visible = true)
            Timber.d("SettingScreen 회원탈퇴 버튼 클릭")
        },
    )
}

@Composable
private fun SettingScreen(
    snackbarHostState: SnackbarHostState,
    onClickBack: () -> Unit,
    onClickInquiry: () -> Unit,
    onClickPrivacyPolicy: () -> Unit,
    onClickLogin: () -> Unit,
    onClickLogout: () -> Unit,
    onClickWithdraw: () -> Unit,
) {
    Scaffold(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.White)
                .systemBarsPadding(),
        topBar = {
            TuripAppBar(
                canBack = true,
                onBackNavigate = onClickBack,
            )
        },
        snackbarHost = { TuripSnackbar(snackbarHostState = snackbarHostState) },
    ) { innerPadding ->
        SettingScreenContent(
            onClickInquiry = onClickInquiry,
            onClickPrivacyPolicy = onClickPrivacyPolicy,
            onClickLogin = onClickLogin,
            onClickLogout = onClickLogout,
            onClickWithdraw = onClickWithdraw,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun SettingScreenContent(
    onClickInquiry: () -> Unit,
    onClickPrivacyPolicy: () -> Unit,
    onClickLogin: () -> Unit,
    onClickLogout: () -> Unit,
    onClickWithdraw: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        SettingCommonScreen(onClickInquiry, onClickPrivacyPolicy)

        when (AuthState.type) {
            UserType.MEMBER -> {
                SettingForMemberScreen(onClickLogout, onClickWithdraw)
            }

            UserType.GUEST -> {
                SettingForGuestScreen(onClickLogin)
            }

            UserType.NONE -> {
                Timber.e("멤버가 지정되지 않았습니다.")
                SettingForGuestScreen(onClickLogin)
            }
        }
    }
}

@Composable
private fun SettingCommonScreen(
    onClickInquiry: () -> Unit,
    onClickPrivacyPolicy: () -> Unit,
) {
    HorizontalDivider(
        thickness = 8.dp,
        color = colorResource(R.color.gray_100_f0f0ee),
    )

    SettingItem(
        imageRes = R.drawable.ic_inquire,
        titleRes = R.string.setting_inquiry,
        onClick = onClickInquiry,
        modifier = Modifier.fillMaxWidth(),
    )
    SettingItem(
        imageRes = R.drawable.ic_document,
        titleRes = R.string.setting_privacy_policy,
        onClick = onClickPrivacyPolicy,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun SettingForGuestScreen(onClickLogin: () -> Unit) {
    SettingItem(
        imageRes = R.drawable.ic_login,
        titleRes = R.string.setting_login,
        onClick = onClickLogin,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun SettingForMemberScreen(
    onClickLogout: () -> Unit,
    onClickWithdraw: () -> Unit,
) {
    SettingItem(
        imageRes = R.drawable.ic_logout,
        titleRes = R.string.setting_logout,
        onClick = onClickLogout,
        modifier = Modifier.fillMaxWidth(),
    )
    SettingItem(
        imageRes = R.drawable.ic_withdraw,
        titleRes = R.string.setting_withdraw,
        onClick = onClickWithdraw,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Preview(showBackground = true)
@Composable
private fun MemberSettingScreenPreview() {
    TuripTheme {
        SettingScreenContent(
            onClickInquiry = {},
            onClickPrivacyPolicy = {},
            onClickLogin = {},
            onClickLogout = {},
            onClickWithdraw = {},
        )
    }
}
