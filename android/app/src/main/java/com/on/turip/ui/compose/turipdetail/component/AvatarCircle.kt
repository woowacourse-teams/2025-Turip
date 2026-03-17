package com.on.turip.ui.compose.turipdetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

internal val AVATAR_SIZE = 48.dp
@Composable
fun AvatarCircle(
    label: Char,
    modifier: Modifier = Modifier,
    backgroundColor: Color = TuripTheme.colors.gray05,
    textColor: Color = TuripTheme.colors.white,
) {
    Box(
        modifier =
            modifier
                .size(AVATAR_SIZE)
                .clip(CircleShape)
                .background(backgroundColor)
                .border(width = 2.dp, color = TuripTheme.colors.white, shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label.toString(),
            color = textColor,
            style = TuripTheme.typography.title1,
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun AvatarCirclePreview() {
    TuripTheme{
        AvatarCircle(
            label = '유',
            modifier = Modifier.size(AVATAR_SIZE)
        )
    }
}