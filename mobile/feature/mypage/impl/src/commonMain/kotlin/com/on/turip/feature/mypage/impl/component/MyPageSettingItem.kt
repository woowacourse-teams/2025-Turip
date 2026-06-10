package com.on.turip.feature.mypage.impl.component

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
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.ic_inquire
import com.on.turip.core.designsystem.generated.resources.my_page_inquiry
import com.on.turip.core.designsystem.theme.TuripTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

@Composable
fun MyPageSettingItem(
    imageRes: DrawableResource,
    titleRes: StringResource,
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
                imageRes = Res.drawable.ic_inquire,
                titleRes = Res.string.my_page_inquiry,
                onClick = {},
                modifier = Modifier.padding(TuripTheme.spacing.small),
            )

            MyPageSettingItem(
                imageRes = Res.drawable.ic_inquire,
                titleRes = Res.string.my_page_inquiry,
                onClick = {},
                modifier = Modifier.padding(TuripTheme.spacing.small),
                contentColor = TuripTheme.colors.error,
            )
        }
    }
}
