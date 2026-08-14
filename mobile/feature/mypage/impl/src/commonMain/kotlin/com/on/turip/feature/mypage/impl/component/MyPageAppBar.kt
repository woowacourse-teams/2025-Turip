package com.on.turip.feature.mypage.impl.component

import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.component.TuripAppBar
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.my_page_notification_setting
import com.on.turip.core.designsystem.generated.resources.my_page_title
import com.on.turip.core.designsystem.theme.TuripTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun MyPageAppBar(onNotificationSettingClick: () -> Unit) {
    TuripAppBar(
        start = {
            Text(
                text = stringResource(Res.string.my_page_title),
                style = TuripTheme.typography.display,
            )
        },
        end = {
            IconButton(
                onClick = onNotificationSettingClick,
                modifier = Modifier.offset(x = 8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(Res.string.my_page_notification_setting),
                    tint = TuripTheme.colors.black,
                )
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun MyPageAppBarPreView() {
    TuripTheme {
        MyPageAppBar(onNotificationSettingClick = {})
    }
}
