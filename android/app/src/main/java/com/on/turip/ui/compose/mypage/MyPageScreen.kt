package com.on.turip.ui.compose.mypage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.mypage.component.BookmarkedContentSection
import com.on.turip.ui.compose.mypage.component.MyPageAppBar
import com.on.turip.ui.compose.mypage.component.MyPageSettingsSection
import com.on.turip.ui.compose.mypage.component.ProfileSection

@Composable
fun MyPageScreen() {
    Scaffold(
        topBar = { MyPageAppBar() },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding =
                PaddingValues(
                    top = TuripTheme.spacing.extraLarge,
                    bottom = TuripTheme.spacing.medium,
                ),
            verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.extraExtraLarge),
        ) {
            item {
                ProfileSection(
                    nickname = "닉네임 넣어주기 필요 123123123123123123123123123123123123123123",
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = TuripTheme.spacing.extraLarge,
                                vertical = TuripTheme.spacing.small,
                            ),
                )
            }
            item {
                BookmarkedContentSection(
                    contents = emptyList(),
                    onViewAllContentClick = {},
                    onContentClick = { },
                    onRemoveBookmark = {},
                )
            }

            item {
                MyPageSettingsSection(
                    onInquiryClick = { },
                    onPrivacyPolicyClick = { },
                    onLoginClick = { },
                    onLogoutClick = { },
                    onWithdrawClick = { },
                    modifier = Modifier.padding(horizontal = TuripTheme.spacing.medium),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MyPageScreenPreview() {
    TuripTheme {
        MyPageScreen()
    }
}
