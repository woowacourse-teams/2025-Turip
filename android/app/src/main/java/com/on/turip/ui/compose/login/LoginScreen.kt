package com.on.turip.ui.compose.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.ui.compose.common.component.HelpText
import com.on.turip.ui.compose.theme.TuripTypography

@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
    var isHelpTextVisible by rememberSaveable { mutableStateOf(false) }
    Scaffold { innerPadding ->
        LoginScreenContent(
            isHelpTextVisible = isHelpTextVisible,
            modifier = modifier.padding(innerPadding),
            onClickHelpText = {
                isHelpTextVisible = true
            },
        )
    }
}

@Composable
fun LoginScreenContent(
    isHelpTextVisible: Boolean,
    onClickHelpText: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
            modifier =
                Modifier
                    .fillMaxWidth(),
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
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            if (isHelpTextVisible) {
                Text(
                    text = "게스트 모드를 이용할 경우,\n사용할 수 있는 기능이 제한돼요",
                    color = Color.White,
                    style = TuripTypography.bodyLarge,
                    modifier =
                        Modifier
                            .border(
                                border =
                                    BorderStroke(
                                        1.dp,
                                        colorResource(R.color.gray_200_c1c1c1),
                                    ),
                                shape = RoundedCornerShape(10.dp),
                            )
                            .background(
                                color = colorResource(R.color.gray_300_5b5b5b),
                                shape = RoundedCornerShape(10.dp),
                            )
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                    textAlign = TextAlign.Center,
                )
            }

            GoogleLoginButton()
            HelpText(
                text = "게스트 모드로 시작하기",
                style = TuripTypography.bodyLarge,
                color = Color.White,
                onClickHelp = onClickHelpText,
            )
        }
    }
}

@Composable
@Preview(showBackground = true, name = "HelpVisible")
private fun HelpVisibleLoginScreenPreview() {
    LoginScreenContent(true, {})
}

@Composable
@Preview(showBackground = true, name = "HelpInvisible")
private fun HelpInvisibleLoginScreenPreview() {
    LoginScreenContent(false, {})
}
