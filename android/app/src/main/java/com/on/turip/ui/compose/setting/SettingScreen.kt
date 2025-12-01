package com.on.turip.ui.compose.setting

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.on.turip.R
import com.on.turip.domain.userstorage.TuripDeviceIdentifier
import com.on.turip.ui.common.model.MemberStatus
import com.on.turip.ui.compose.common.component.TuripAppBar
import com.on.turip.ui.compose.common.component.TuripDialog
import com.on.turip.ui.compose.theme.TuripTheme
import com.on.turip.ui.main.favorite.SettingViewModel
import timber.log.Timber

@Composable
fun SettingScreen(
    navigateToBack: () -> Unit,
    navigateToInquiry: (Uri) -> Unit,
    navigateToPrivacyPolicy: (Uri) -> Unit,
    navigateToLoginScreen: () -> Unit,
    viewModel: SettingViewModel = hiltViewModel(),
) {
    val uiState: SettingUiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                SettingUiEvent.Logout, SettingUiEvent.Withdraw -> navigateToLoginScreen()
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
            onConfirmation = viewModel::confirmLogout,
            onDismissRequest = { viewModel.showLogoutDialog(show = false) },
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
            onConfirmation = viewModel::confirmWithdraw,
            onDismissRequest = { viewModel.showWithdrawDialog(show = false) },
        )
    }

    SettingScreen(
        uiState = uiState,
        onClickBack = navigateToBack,
        onClickInquiry = {
            navigateToInquiry(viewModel.loadInquiryUri())
            Timber.d("SettingScreen 문의하기 버튼 클릭")
        },
        onClickPrivacyPolicy = {
            navigateToPrivacyPolicy(viewModel.loadPrivacyPolicyUri())
            Timber.d("SettingScreen 개인정보처리 방침 버튼 클릭")
        },
        onClickLogin = {
            navigateToLoginScreen()
            Timber.d("SettingScreen 로그인 버튼 클릭")
        },
        onClickLogout = {
            // TODO : ViewModel 에서 로그아웃 로직 처리 후 로그인 화면으로 이동
            viewModel.showLogoutDialog(show = true)
            Timber.d("SettingScreen 로그아웃 버튼 클릭")
        },
        onClickWithdraw = {
            // TODO : ViewModel 에서 회원탈퇴 로직 처리 후 로그인 화면으로 이동
            viewModel.showWithdrawDialog(show = true)
            Timber.d("SettingScreen 회원탈퇴 버튼 클릭")
        },
    )
}

@Composable
private fun SettingScreen(
    uiState: SettingUiState,
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
    ) { innerPadding ->
        SettingScreenContent(
            uiState = uiState,
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
    uiState: SettingUiState,
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

        when (uiState.memberStatus) {
            MemberStatus.MEMBER -> {
                SettingForMemberScreen(onClickLogout, onClickWithdraw)
            }

            MemberStatus.GUEST -> {
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
        uiModel =
            SettingModel(
                iconResource = R.drawable.ic_inquire,
                titleResource = R.string.setting_inquiry,
            ),
        onClick = onClickInquiry,
        modifier = Modifier.fillMaxWidth(),
    )
    SettingItem(
        uiModel =
            SettingModel(
                iconResource = R.drawable.ic_document,
                titleResource = R.string.setting_privacy_policy,
            ),
        onClick = onClickPrivacyPolicy,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun SettingForGuestScreen(onClickLogin: () -> Unit) {
    SettingItem(
        uiModel =
            SettingModel(
                iconResource = R.drawable.ic_login,
                titleResource = R.string.setting_login,
            ),
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
        uiModel =
            SettingModel(
                iconResource = R.drawable.ic_logout,
                titleResource = R.string.setting_logout,
            ),
        onClick = onClickLogout,
        modifier = Modifier.fillMaxWidth(),
    )
    SettingItem(
        uiModel =
            SettingModel(
                iconResource = R.drawable.ic_withdraw,
                titleResource = R.string.setting_withdraw,
            ),
        onClick = onClickWithdraw,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Preview(showBackground = true, name = "멤버")
@Composable
private fun MemberSettingScreenPreview() {
    TuripTheme {
        SettingScreenContent(
            uiState =
                SettingUiState(
                    deviceIdentifier = TuripDeviceIdentifier.EMPTY,
                    memberStatus = MemberStatus.MEMBER,
                    showLogoutDialog = false,
                    showWithdrawDialog = false,
                ),
            onClickInquiry = {},
            onClickPrivacyPolicy = {},
            onClickLogin = {},
            onClickLogout = {},
            onClickWithdraw = {},
        )
    }
}

@Preview(showBackground = true, name = "게스트")
@Composable
private fun GuestSettingScreenPreview() {
    TuripTheme {
        SettingScreenContent(
            uiState =
                SettingUiState(
                    deviceIdentifier = TuripDeviceIdentifier.EMPTY,
                    memberStatus = MemberStatus.GUEST,
                    showLogoutDialog = false,
                    showWithdrawDialog = false,
                ),
            onClickInquiry = {},
            onClickPrivacyPolicy = {},
            onClickLogin = {},
            onClickLogout = {},
            onClickWithdraw = {},
        )
    }
}
