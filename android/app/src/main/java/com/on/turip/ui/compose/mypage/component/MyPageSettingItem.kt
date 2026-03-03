package com.on.turip.ui.compose.mypage.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

@Composable
fun MyPageSettingItem(
    @DrawableRes imageRes: Int,
    @StringRes titleRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentColor: Color = TuripTheme.colors.gray04,
) {
    val shape = TuripTheme.shape.container

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(shape)
                .clickable(onClick = onClick)
                .background(color = TuripTheme.colors.gray01.copy(alpha = 0.2f))
                .border(
                    width = 1.dp,
                    color = TuripTheme.colors.gray02,
                    shape = shape,
                ).padding(
                    horizontal = TuripTheme.spacing.extraLarge,
                    vertical = TuripTheme.spacing.large,
                ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(imageRes),
            tint = contentColor,
            contentDescription = null,
            modifier =
                Modifier
                    .padding(end = TuripTheme.spacing.large)
                    .size(20.dp),
        )
        Text(
            text = stringResource(titleRes),
            style = TuripTheme.typography.info1,
            color = contentColor,
            textAlign = TextAlign.Left,
            modifier =
                Modifier
                    .weight(1f)
                    .padding(end = TuripTheme.spacing.medium),
        )

        Icon(
            imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
            tint = TuripTheme.colors.gray02,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MyPageSettingItemPreview() {
    TuripTheme {
        Column {
            MyPageSettingItem(
                imageRes = R.drawable.ic_inquire,
                titleRes = R.string.my_page_inquiry,
                onClick = {},
                modifier = Modifier.padding(TuripTheme.spacing.small),
            )

            MyPageSettingItem(
                imageRes = R.drawable.ic_inquire,
                titleRes = R.string.my_page_inquiry,
                onClick = {},
                modifier = Modifier.padding(TuripTheme.spacing.small),
                contentColor = TuripTheme.colors.error,
            )
        }
    }
}
