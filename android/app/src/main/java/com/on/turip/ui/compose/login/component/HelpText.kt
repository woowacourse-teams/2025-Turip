package com.on.turip.ui.compose.login.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.ui.compose.theme.TuripTypography

@Composable
fun HelpText(
    text: String,
    style: TextStyle,
    color: Color,
    onClickIcon: () -> Unit,
    onClickText: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = style,
            color = color,
            textDecoration = TextDecoration.Underline,
            modifier =
                Modifier.clickable {
                    onClickText()
                },
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Image(
            painter = painterResource(R.drawable.btn_help),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color),
            modifier =
                Modifier.clickable {
                    onClickIcon()
                },
        )
    }
}

@Preview
@Composable
private fun HelpTextPreview() {
    HelpText("도움말", TuripTypography.titleLarge, Color.White, {}, {})
}
