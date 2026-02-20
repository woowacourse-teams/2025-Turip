package com.on.turip.ui.compose.mypage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.on.turip.R
import com.on.turip.common.AuthState
import com.on.turip.common.UserType
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import timber.log.Timber

@Composable
fun MyPageSettingsSection(
    onInquiryClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(bottom = TuripTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.extraSmall),
    ) {
        Text(
            text = stringResource(R.string.my_page_settings_title),
            style = TuripTheme.typography.title3,
            color = TuripTheme.colors.gray03,
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.extraSmall))

        SettingForAll(onInquiryClick, onPrivacyPolicyClick)

        when (AuthState.type) {
            UserType.MEMBER -> {
                SettingForMember(onLogoutClick, onWithdrawClick)
            }

            UserType.GUEST -> {
                SettingForGuest(onLoginClick)
            }

            UserType.NONE -> {
                Timber.e("멤버가 지정되지 않았습니다.")
                SettingForGuest(onLoginClick)
            }
        }
    }
}

@Composable
private fun SettingForAll(
    onInquiryClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
) {
    MyPageSettingItem(
        imageRes = R.drawable.ic_inquire,
        titleRes = R.string.my_page_inquiry,
        onClick = onInquiryClick,
    )
    MyPageSettingItem(
        imageRes = R.drawable.ic_document,
        titleRes = R.string.my_page_privacy_policy,
        onClick = onPrivacyPolicyClick,
    )
}

@Composable
private fun SettingForGuest(onLoginClick: () -> Unit) {
    MyPageSettingItem(
        imageRes = R.drawable.ic_account,
        titleRes = R.string.my_page_login,
        onClick = onLoginClick,
    )
}

@Composable
private fun SettingForMember(
    onLogoutClick: () -> Unit,
    onWithdrawClick: () -> Unit,
) {
    MyPageSettingItem(
        imageRes = R.drawable.ic_account,
        titleRes = R.string.my_page_logout,
        onClick = onLogoutClick,
    )
    MyPageSettingItem(
        imageRes = R.drawable.ic_withdraw,
        titleRes = R.string.my_page_withdraw,
        onClick = onWithdrawClick,
    )
}

@Preview(showBackground = true)
@Composable
private fun MyPageSettingsSectionPreview() {
    TuripTheme {
        MyPageSettingsSection(
            onInquiryClick = {},
            onPrivacyPolicyClick = {},
            onLoginClick = {},
            onLogoutClick = {},
            onWithdrawClick = {},
            modifier =
                Modifier
                    .background(color = TuripTheme.colors.white)
                    .padding(TuripTheme.spacing.large),
        )
    }
}
