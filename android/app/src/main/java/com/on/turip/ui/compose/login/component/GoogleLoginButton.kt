package com.on.turip.ui.compose.login.component

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

@Composable
fun GoogleLoginButton(
    onClickLoginButton: () -> Unit,
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
                .clickable { onClickLoginButton() }
                .padding(vertical = 16.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_google),
            contentDescription = null,
            modifier = Modifier.size(28.dp),
        )
        Text(
            text = stringResource(R.string.login_google_description),
            style = TuripTheme.typography.title2,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(end = 28.dp),
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = false)
@Composable
private fun GoogleLoginButtonPreView() {
    TuripTheme {
        GoogleLoginButton(
            onClickLoginButton = {},
            modifier =
                Modifier.padding(20.dp),
        )
    }
}
