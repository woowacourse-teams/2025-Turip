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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.bg_login
import com.on.turip.core.designsystem.generated.resources.ic_logo
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.feature.login.impl.component.GoogleLoginButton
import com.on.turip.feature.login.impl.component.GuestModeSection
import com.on.turip.feature.login.impl.util.noRippleClickable
import org.jetbrains.compose.resources.painterResource

@Composable
fun LoginScreen(
    deepLinkUrl: String?,
    onNavigateToMain: (deepLinkUrl: String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isHelpTextVisible: Boolean by rememberSaveable { mutableStateOf(false) }

    LoginScreenContent(
        isHelpTextVisible = isHelpTextVisible,
        modifier =
            modifier
                .fillMaxSize()
                .noRippleClickable { isHelpTextVisible = false },
        onHelpClick = { isHelpTextVisible = !isHelpTextVisible },
        onGoogleLoginClick = { onNavigateToMain(deepLinkUrl) },
        onGuestLoginClick = { onNavigateToMain(deepLinkUrl) },
    )
}

@Composable
private fun LoginScreenContent(
    isHelpTextVisible: Boolean,
    onHelpClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
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
                text = "직진만 남은 여행",
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
                    text = "게스트 모드를 이용할 경우,\n사용할 수 있는 기능이 제한돼요",
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
            )

            GuestModeSection(
                text = "게스트 모드로 시작하기",
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
            onHelpClick = {},
            onGoogleLoginClick = {},
            onGuestLoginClick = {},
        )
    }
}
