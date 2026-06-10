package com.on.turip.feature.login.impl.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.ic_google
import com.on.turip.core.designsystem.generated.resources.login_google_description
import com.on.turip.core.designsystem.theme.TuripTheme

@Composable
fun GoogleLoginButton(
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .border(
                    border = BorderStroke(1.dp, TuripTheme.colors.gray02),
                    shape = RoundedCornerShape(30.dp),
                ).background(color = TuripTheme.colors.white, shape = RoundedCornerShape(30.dp))
                .clip(RoundedCornerShape(30.dp))
                .clickable { onLoginClick() }
                .padding(vertical = TuripTheme.spacing.large, horizontal = TuripTheme.spacing.large),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_google),
            contentDescription = null,
            modifier = Modifier.size(TuripTheme.spacing.huge),
        )
        Text(
            text = stringResource(Res.string.login_google_description),
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
private fun GoogleLoginButtonPreView() {
    TuripTheme {
        GoogleLoginButton(
            onLoginClick = {},
            modifier =
                Modifier.padding(20.dp),
        )
    }
}
