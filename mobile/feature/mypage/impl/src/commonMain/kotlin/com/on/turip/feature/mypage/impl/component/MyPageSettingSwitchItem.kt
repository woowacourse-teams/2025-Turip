package com.on.turip.feature.mypage.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.my_page_push_notification
import com.on.turip.core.designsystem.theme.TuripTheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun MyPageSettingSwitchItem(
    titleRes: StringResource,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    imageVector: ImageVector = Icons.Filled.Notifications,
) {
    val shape = TuripTheme.shape.container

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(shape)
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
            imageVector = imageVector,
            tint = TuripTheme.colors.gray04,
            contentDescription = null,
            modifier =
                Modifier
                    .padding(end = TuripTheme.spacing.large)
                    .size(20.dp),
        )
        Text(
            text = stringResource(titleRes),
            style = TuripTheme.typography.info1,
            color = TuripTheme.colors.gray04,
            textAlign = TextAlign.Left,
            modifier =
                Modifier
                    .weight(1f)
                    .padding(end = TuripTheme.spacing.medium),
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors =
                SwitchDefaults.colors(
                    checkedTrackColor = TuripTheme.colors.primary,
                ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MyPageSettingSwitchItemPreview() {
    TuripTheme {
        MyPageSettingSwitchItem(
            titleRes = Res.string.my_page_push_notification,
            checked = true,
            onCheckedChange = {},
            modifier = Modifier.padding(TuripTheme.spacing.small),
        )
    }
}
