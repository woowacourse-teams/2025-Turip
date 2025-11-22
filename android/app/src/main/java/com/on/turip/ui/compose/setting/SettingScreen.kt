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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.ui.compose.common.component.TuripAppBar

@Composable
fun SettingScreen(
    loginStatus: LoginStatus,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color.White)
                .systemBarsPadding(),
        topBar = { TuripAppBar(true) },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            HorizontalDivider(
                thickness = 8.dp,
                color = colorResource(R.color.gray_100_f0f0ee),
            )

            SettingItem(
                onClick = {},
                icon = R.drawable.ic_inquire,
                title = "문의하기",
                modifier = Modifier.fillMaxWidth(),
            )
            SettingItem(
                onClick = {},
                icon = R.drawable.ic_document,
                title = "개인 정보 처리방침",
                modifier = Modifier.fillMaxWidth(),
            )
            when (loginStatus) {
                LoginStatus.MEMBER -> {
                    SettingItem(
                        onClick = {},
                        icon = R.drawable.ic_logout,
                        title = "로그아웃",
                        modifier = Modifier.fillMaxWidth(),
                    )
                    SettingItem(
                        onClick = {},
                        icon = R.drawable.ic_withdraw,
                        title = "회원 탈퇴",
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                LoginStatus.GUEST -> {
                    SettingItem(
                        onClick = {},
                        icon = R.drawable.ic_login,
                        title = "로그인",
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

enum class LoginStatus {
    MEMBER,
    GUEST,
}

@Preview(showBackground = true, name = "멤버")
@Composable
private fun MemberSettingScreenPreview() {
    SettingScreen(
        loginStatus = LoginStatus.MEMBER,
    )
}

@Preview(showBackground = true, name = "게스트")
@Composable
private fun GuestSettingScreenPreview() {
    SettingScreen(
        loginStatus = LoginStatus.GUEST,
    )
}
