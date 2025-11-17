package com.on.turip.ui.compose.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.on.turip.R
import com.on.turip.ui.compose.theme.TuripTheme
import com.on.turip.ui.compose.theme.TuripTypography

@Composable
fun GoogleLoginButton(modifier: Modifier = Modifier) {
    Row(
        modifier =
            modifier
                .border(border = BorderStroke(1.dp, colorResource(R.color.turip_gray_b4b4b4)), shape = RoundedCornerShape(30.dp))
                .background(color = Color.White, shape = RoundedCornerShape(30.dp))
                .padding(vertical = 16.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_google),
            contentDescription = null,
        )
        Spacer(
            modifier = Modifier.padding(10.dp),
        )
        Text(
            text = "구글 로그인",
            style = TuripTypography.titleMedium,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(end = 10.dp),
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = false)
@Composable
private fun GoogleLoginButtonPreView() {
    TuripTheme {
        GoogleLoginButton(
            modifier =
                Modifier
                    .padding(20.dp),
        )
    }
}
