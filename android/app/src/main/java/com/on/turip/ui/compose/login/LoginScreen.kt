package com.on.turip.ui.compose.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.ui.compose.common.component.HelpText
import com.on.turip.ui.compose.theme.TuripTypography

@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
    Scaffold { innerPadding ->
        Box(
            modifier = modifier,
        ) {
            Image(
                painter = painterResource(R.drawable.bg_login),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )

            Column(
                modifier = Modifier.padding(innerPadding).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_logo),
                    contentDescription = null,
                    modifier =
                        Modifier
                            .padding(top = 32.dp, start = 23.dp)
                            .size(width = 160.dp, height = 61.dp),
                )

                Text(
                    text = "직진만 남은 여행",
                    style = TuripTypography.titleSmall,
                    modifier = Modifier.padding(top = 5.dp),
                )
            }

            Column(
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(start = 20.dp, end = 20.dp, bottom = 44.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                GoogleLoginButton(Modifier.padding(bottom = 14.dp))
                HelpText(
                    text = "게스트 모드로 시작하기",
                    style = TuripTypography.bodyLarge,
                    color = Color.White,
                    onClickHelp = {},
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun LoginScreenPreview() {
    LoginScreen()
}
