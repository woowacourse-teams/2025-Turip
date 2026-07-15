package com.on.turip.feature.mypage.impl.notificationsetting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.component.TuripAppBar
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.my_page_notification_setting
import com.on.turip.core.designsystem.generated.resources.my_page_push_notification
import com.on.turip.core.designsystem.generated.resources.my_page_push_notification_description
import com.on.turip.core.designsystem.theme.TuripTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun NotificationSettingScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isPushNotificationEnabled: Boolean by remember { mutableStateOf(true) }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(TuripTheme.colors.white)
                .systemBarsPadding(),
    ) {
        TuripAppBar(
            start = {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(36.dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = TuripTheme.colors.black,
                    )
                }
            },
            center = {
                Text(
                    text = stringResource(Res.string.my_page_notification_setting),
                    style = TuripTheme.typography.title1,
                    color = TuripTheme.colors.black,
                )
            },
        )

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(TuripTheme.spacing.extraLarge),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(Res.string.my_page_push_notification),
                    style = TuripTheme.typography.title2,
                    color = TuripTheme.colors.black,
                )

                Spacer(modifier = Modifier.height(TuripTheme.spacing.extraSmall))

                Text(
                    text = stringResource(Res.string.my_page_push_notification_description),
                    style = TuripTheme.typography.info1,
                    color = TuripTheme.colors.gray03,
                )
            }

            Switch(
                checked = isPushNotificationEnabled,
                onCheckedChange = { isPushNotificationEnabled = it },
                colors =
                    SwitchDefaults.colors(
                        checkedTrackColor = TuripTheme.colors.primary,
                    ),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationSettingScreenPreview() {
    TuripTheme {
        NotificationSettingScreen(onBackClick = {})
    }
}
