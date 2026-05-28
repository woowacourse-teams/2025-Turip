package com.on.turip.feature.mypage.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.theme.TuripTheme

@Composable
fun MyPageSettingsSection(
    isLoggedIn: Boolean,
    onInquiryClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = TuripTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.extraSmall),
    ) {
        Text(
            text = "설정",
            style = TuripTheme.typography.title3,
            color = TuripTheme.colors.gray03,
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.extraSmall))

        SettingItem(
            icon = Icons.Default.Email,
            title = "문의하기",
            onClick = onInquiryClick,
        )
        SettingItem(
            icon = Icons.Default.Description,
            title = "개인정보 처리방침",
            onClick = onPrivacyPolicyClick,
        )

        if (isLoggedIn) {
            SettingItem(
                icon = Icons.Default.AccountCircle,
                title = "로그아웃",
                onClick = onLogoutClick,
            )
            SettingItem(
                icon = Icons.Default.ExitToApp,
                title = "회원 탈퇴",
                onClick = onWithdrawClick,
                contentColor = TuripTheme.colors.error,
            )
        } else {
            SettingItem(
                icon = Icons.Default.AccountCircle,
                title = "로그인",
                onClick = onLoginClick,
            )
        }
    }
}

@Composable
private fun SettingItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentColor: Color = TuripTheme.colors.gray04,
) {
    val shape = TuripTheme.shape.container

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .clickable(onClick = onClick)
            .background(color = TuripTheme.colors.gray01.copy(alpha = 0.2f))
            .border(width = 1.dp, color = TuripTheme.colors.gray02, shape = shape)
            .padding(horizontal = TuripTheme.spacing.extraLarge, vertical = TuripTheme.spacing.large),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            tint = contentColor,
            contentDescription = null,
            modifier = Modifier
                .padding(end = TuripTheme.spacing.large)
                .size(20.dp),
        )
        Text(
            text = title,
            style = TuripTheme.typography.info1,
            color = contentColor,
            textAlign = TextAlign.Left,
            modifier = Modifier
                .weight(1f)
                .padding(end = TuripTheme.spacing.medium),
        )
        Icon(
            imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
            tint = TuripTheme.colors.gray02,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
        )
    }
}
