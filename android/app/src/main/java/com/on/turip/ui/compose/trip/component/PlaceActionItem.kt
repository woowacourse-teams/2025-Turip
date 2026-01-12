package com.on.turip.ui.compose.trip.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

@Composable
fun PlaceActionItem(
    text: String,
    @DrawableRes drawableRes: Int,
    useTint: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = TuripTheme.colors.gray04,
    iconTint: Color = TuripTheme.colors.gray04,
    iconSize: Dp = 24.dp,
) {
    Row(
        modifier =
            modifier
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (useTint) {
            Icon(
                painter = painterResource(drawableRes),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(iconSize),
            )
        } else {
            Image(
                painter = painterResource(drawableRes),
                contentDescription = null,
                modifier = Modifier.size(iconSize),
            )
        }
        Text(
            text = text,
            style = TuripTheme.typography.info1,
            color = textColor,
        )
    }
}

@Preview(showBackground = true, name = "아이콘")
@Composable
private fun IconPlaceActionItemPreview() {
    TuripTheme {
        PlaceActionItem(
            text = "01:03~",
            drawableRes = R.drawable.ic_play_button,
            useTint = true,
            onClick = {},
        )
    }
}

@Preview(showBackground = true, name = "이미지")
@Composable
private fun ImagePlaceActionItemPreview() {
    TuripTheme {
        PlaceActionItem(
            text = "01:03~",
            drawableRes = R.drawable.btn_kakao_map_basic,
            useTint = false,
            onClick = {},
        )
    }
}
