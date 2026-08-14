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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.on.turip.core.designsystem.component.TuripAppBar
import com.on.turip.core.designsystem.component.TuripDialog
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.all_back_description
import com.on.turip.core.designsystem.generated.resources.all_close_description
import com.on.turip.core.designsystem.generated.resources.my_page_notification_permission_banner
import com.on.turip.core.designsystem.generated.resources.my_page_notification_permission_dialog_confirm
import com.on.turip.core.designsystem.generated.resources.my_page_notification_permission_dialog_dismiss
import com.on.turip.core.designsystem.generated.resources.my_page_notification_permission_dialog_message
import com.on.turip.core.designsystem.generated.resources.my_page_notification_permission_dialog_title
import com.on.turip.core.designsystem.generated.resources.my_page_notification_load_failed
import com.on.turip.core.designsystem.generated.resources.my_page_notification_permission_go_to_settings
import com.on.turip.core.designsystem.generated.resources.my_page_notification_setting
import com.on.turip.core.designsystem.generated.resources.my_page_notification_update_failed
import com.on.turip.core.designsystem.generated.resources.my_page_push_notification
import com.on.turip.core.designsystem.generated.resources.my_page_push_notification_description
import com.on.turip.core.designsystem.snackbar.LocalSnackbarDelegate
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.feature.mypage.impl.notificationsetting.platform.rememberNotificationPermissionActions
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NotificationSettingScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotificationSettingViewModel = koinViewModel(),
) {
    val uiState: NotificationSettingState by viewModel.uiState.collectAsStateWithLifecycle()
    val permissionActions = rememberNotificationPermissionActions()
    val coroutineScope = rememberCoroutineScope()
    val snackbarDelegate = LocalSnackbarDelegate.current

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        coroutineScope.launch {
            val granted = permissionActions.isNotificationsEnabled()
            viewModel.onIntent(NotificationSettingIntent.UpdateSystemPermission(granted))
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                NotificationSettingEffect.OpenNotificationSettings -> {
                    permissionActions.openNotificationSettings()
                }

                NotificationSettingEffect.ShowUpdateFailed -> {
                    snackbarDelegate.showSnackbar(
                        message = getString(Res.string.my_page_notification_update_failed),
                        actionLabel = getString(Res.string.all_close_description),
                        duration = SnackbarDuration.Short,
                    )
                }

                NotificationSettingEffect.ShowLoadFailed -> {
                    snackbarDelegate.showSnackbar(
                        message = getString(Res.string.my_page_notification_load_failed),
                        actionLabel = getString(Res.string.all_close_description),
                        duration = SnackbarDuration.Short,
                    )
                }
            }
        }
    }

    uiState.dialogState?.let { dialogState ->
        when (dialogState) {
            NotificationSettingDialogState.SystemNotificationDisabled -> {
                TuripDialog(
                    title = stringResource(Res.string.my_page_notification_permission_dialog_title),
                    message = stringResource(Res.string.my_page_notification_permission_dialog_message),
                    confirmText = stringResource(Res.string.my_page_notification_permission_dialog_confirm),
                    dismissText = stringResource(Res.string.my_page_notification_permission_dialog_dismiss),
                    onConfirmation = { viewModel.onIntent(NotificationSettingIntent.ClickGoToSettings) },
                    onDismissRequest = { viewModel.onIntent(NotificationSettingIntent.DismissDialog) },
                )
            }
        }
    }

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
                        contentDescription = stringResource(Res.string.all_back_description),
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

        Column(modifier = Modifier.padding(TuripTheme.spacing.extraLarge)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                    checked = uiState.isPushNotificationEnabled,
                    enabled = !uiState.isLoading,
                    onCheckedChange = { checked ->
                        viewModel.onIntent(NotificationSettingIntent.ToggleNotification(checked))
                    },
                    colors =
                        SwitchDefaults.colors(
                            checkedTrackColor = TuripTheme.colors.primary,
                        ),
                )
            }

            if (uiState.shouldShowSystemNotificationBanner) {
                Spacer(modifier = Modifier.height(TuripTheme.spacing.small))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(Res.string.my_page_notification_permission_banner),
                        style = TuripTheme.typography.info2,
                        color = TuripTheme.colors.error,
                        modifier = Modifier.weight(1f),
                    )

                    TextButton(onClick = { viewModel.onIntent(NotificationSettingIntent.ClickGoToSettings) }) {
                        Text(
                            text = stringResource(Res.string.my_page_notification_permission_go_to_settings),
                            style = TuripTheme.typography.info2,
                            color = TuripTheme.colors.primary,
                        )
                    }
                }
            }
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
