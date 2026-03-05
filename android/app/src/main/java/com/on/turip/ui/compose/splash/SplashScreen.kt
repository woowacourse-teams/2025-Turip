package com.on.turip.ui.compose.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    // windowSplashScreenBackground 속성과 통일하기 위해 colors 에 정의된 값을 사용
    val splashBackgroundColor = colorResource(R.color.splash_background)

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(color = splashBackgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_turip_logo_kr),
                contentDescription = null,
                modifier = Modifier.size(120.dp),
            )

            Spacer(modifier = Modifier.height(TuripTheme.spacing.large))

            Text(
                text = stringResource(id = R.string.all_turip_description),
                style = TuripTheme.typography.title3,
                color = TuripTheme.colors.white,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    TuripTheme {
        SplashScreen()
    }
}
