package com.on.turip.feature.login.impl.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.ic_apple
import com.on.turip.core.designsystem.generated.resources.login_apple_description
import com.on.turip.core.designsystem.theme.TuripTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AppleLoginButton(
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                // Apple HIG는 Sign in with Apple 블랙 버튼에 순정 블랙(#000000)을 요구하므로 앱 디자인 토큰(#151515) 대신 하드코딩
                .background(color = Color.Black, shape = RoundedCornerShape(30.dp))
                .clip(RoundedCornerShape(30.dp))
                .clickable { onLoginClick() }
                .padding(vertical = TuripTheme.spacing.large, horizontal = TuripTheme.spacing.large),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_apple),
            contentDescription = null,
            modifier = Modifier.size(TuripTheme.spacing.huge),
        )
        Text(
            text = stringResource(Res.string.login_apple_description),
            color = TuripTheme.colors.white,
            style = TuripTheme.typography.title2,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(end = TuripTheme.spacing.huge),
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = false)
@Composable
private fun AppleLoginButtonPreview() {
    TuripTheme {
        AppleLoginButton(
            onLoginClick = {},
            modifier =
                Modifier.padding(20.dp),
        )
    }
}
