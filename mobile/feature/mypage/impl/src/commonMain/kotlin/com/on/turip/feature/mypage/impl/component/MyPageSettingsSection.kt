package com.on.turip.feature.mypage.impl.component

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
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.ic_account
import com.on.turip.core.designsystem.generated.resources.ic_document
import com.on.turip.core.designsystem.generated.resources.ic_inquire
import com.on.turip.core.designsystem.generated.resources.ic_withdraw
import com.on.turip.core.designsystem.generated.resources.my_page_inquiry
import com.on.turip.core.designsystem.generated.resources.my_page_login
import com.on.turip.core.designsystem.generated.resources.my_page_logout
import com.on.turip.core.designsystem.generated.resources.my_page_privacy_policy
import com.on.turip.core.designsystem.generated.resources.my_page_settings_title
import com.on.turip.core.designsystem.generated.resources.my_page_withdraw
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.domain.session.SessionState

@Composable
fun MyPageSettingsSection(
    sessionState: SessionState,
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
            text = stringResource(Res.string.my_page_settings_title),
            style = TuripTheme.typography.title3,
            color = TuripTheme.colors.gray03,
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.extraSmall))

        SettingForAll(onInquiryClick, onPrivacyPolicyClick)

        when (sessionState) {
            SessionState.Member -> {
                SettingForMember(onLogoutClick, onWithdrawClick)
            }

            SessionState.Guest, SessionState.Uninitialized -> {
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
        imageRes = Res.drawable.ic_inquire,
        titleRes = Res.string.my_page_inquiry,
        onClick = onInquiryClick,
    )
    MyPageSettingItem(
        imageRes = Res.drawable.ic_document,
        titleRes = Res.string.my_page_privacy_policy,
        onClick = onPrivacyPolicyClick,
    )
}

@Composable
private fun SettingForGuest(onLoginClick: () -> Unit) {
    MyPageSettingItem(
        imageRes = Res.drawable.ic_account,
        titleRes = Res.string.my_page_login,
        onClick = onLoginClick,
    )
}

@Composable
private fun SettingForMember(
    onLogoutClick: () -> Unit,
    onWithdrawClick: () -> Unit,
) {
    MyPageSettingItem(
        imageRes = Res.drawable.ic_account,
        titleRes = Res.string.my_page_logout,
        onClick = onLogoutClick,
    )
    MyPageSettingItem(
        imageRes = Res.drawable.ic_withdraw,
        titleRes = Res.string.my_page_withdraw,
        onClick = onWithdrawClick,
        contentColor = TuripTheme.colors.error,
    )
}

@Preview(showBackground = true)
@Composable
private fun MyPageSettingsSectionMemberPreview() {
    TuripTheme {
        MyPageSettingsSection(
            sessionState = SessionState.Member,
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

@Preview(showBackground = true)
@Composable
private fun MyPageSettingsSectionGuestPreview() {
    TuripTheme {
        MyPageSettingsSection(
            sessionState = SessionState.Guest,
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
