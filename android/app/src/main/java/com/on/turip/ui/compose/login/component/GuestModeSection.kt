package com.on.turip.ui.compose.login.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

@Composable
fun GuestModeSection(
    text: String,
    color: Color,
    onHelpClick: () -> Unit,
    onTextClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = TuripTheme.typography.body1,
            color = color,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable(onClick = onTextClick),
        )

        Spacer(modifier = Modifier.width(TuripTheme.spacing.small))

        Image(
            painter = painterResource(R.drawable.btn_help),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color),
            modifier = Modifier.clickable(onClick = onHelpClick),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GuestModeSectionPreview() {
    TuripTheme {
        GuestModeSection(
            text = "게스트모드로 시작하기",
            color = TuripTheme.colors.white,
            onHelpClick = {},
            onTextClick = {},
        )
    }
}
