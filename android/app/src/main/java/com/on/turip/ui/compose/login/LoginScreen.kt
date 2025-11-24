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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.on.turip.R
import com.on.turip.data.login.datasource.GoogleCredentialManager
import com.on.turip.ui.compose.common.component.HelpText
import com.on.turip.ui.compose.common.util.noRippleClickable
import com.on.turip.ui.compose.theme.TuripTypography

@Composable
fun LoginScreen(
    navigateToMain: () -> Unit,
    googleCredentialManager: GoogleCredentialManager,
    modifier: Modifier = Modifier,
    viewmodel: LoginViewmodel = hiltViewModel<LoginViewmodel>(),
) {
    var isHelpTextVisible by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier =
            modifier.noRippleClickable {
                isHelpTextVisible = false
            },
    ) { innerPadding ->
        Image(
            painter = painterResource(R.drawable.bg_login),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        LoginScreenContent(
            isHelpTextVisible = isHelpTextVisible,
            modifier = Modifier.padding(innerPadding),
            onClickHelpText = {
                isHelpTextVisible = !isHelpTextVisible
            },
            navigateToMain = navigateToMain,
            onClickLoginButton = {
                viewmodel.login(googleCredentialManager)
            },
        )
    }
}

@Composable
private fun LoginScreenContent(
    navigateToMain: () -> Unit,
    isHelpTextVisible: Boolean,
    onClickHelpText: () -> Unit,
    onClickLoginButton: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
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
                text = stringResource(R.string.all_turip_description),
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
                    text = stringResource(R.string.login_help_description),
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
                            ).background(
                                color = colorResource(R.color.gray_300_5b5b5b),
                                shape = RoundedCornerShape(10.dp),
                            ).fillMaxWidth()
                            .padding(vertical = 20.dp),
                    textAlign = TextAlign.Center,
                )
            }

            GoogleLoginButton(onClickLoginButton = onClickLoginButton)
            HelpText(
                text = stringResource(R.string.login_start_to_guest),
                style = TuripTypography.bodyLarge,
                color = Color.White,
                onClickIcon = onClickHelpText,
                onClickText = navigateToMain,
            )
        }
    }
}

@Composable
@Preview(showBackground = true, name = "HelpVisible")
private fun HelpVisibleLoginScreenPreview() {
    LoginScreenContent({}, true, {}, {})
}

@Composable
@Preview(showBackground = true, name = "HelpInvisible")
private fun HelpInvisibleLoginScreenPreview() {
    LoginScreenContent({}, false, {}, {})
}
