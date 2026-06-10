package com.on.turip.feature.invitation.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.on.turip.core.designsystem.component.TuripDialog
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.*
import com.on.turip.core.designsystem.theme.TuripTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun InvitationEntryScreen(
    deepLinkUrl: String,
    onNavigateToHome: () -> Unit,
    onNavigateToTuripDetail: (turipId: Long) -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    InvitationEntryScreenContent(
        uiState =
            InvitationEntryUiState(
                deepLinkUrl = deepLinkUrl,
                invitationTuripId = 1L,
                invitationTuripName = "초대받은 튜립",
                dialogState = null,
            ),
        onConfirmEnterInvitedTurip = { onNavigateToTuripDetail(1L) },
        onCancelEnterInvitedTurip = onNavigateToHome,
        onRetryResolveEntry = {},
        onConfirmFailureDialog = onNavigateToHome,
        onResolveInvalidInvitationDialog = onNavigateToHome,
        modifier = modifier,
    )
}

@Composable
private fun InvitationEntryScreenContent(
    uiState: InvitationEntryUiState,
    onConfirmEnterInvitedTurip: () -> Unit,
    onCancelEnterInvitedTurip: () -> Unit,
    onRetryResolveEntry: () -> Unit,
    onConfirmFailureDialog: () -> Unit,
    onResolveInvalidInvitationDialog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val invitationTuripName = uiState.invitationTuripName

    InvitationEntryLoading(
        turipName = invitationTuripName,
        modifier = modifier,
    )

    uiState.invitationTuripId?.let {
        val title =
            invitationTuripName?.let { turipName ->
                stringResource(Res.string.invitation_confirm_dialog_title_with_name, turipName)
            } ?: stringResource(Res.string.invitation_confirm_dialog_title)
        val message =
            invitationTuripName?.let { turipName ->
                stringResource(Res.string.invitation_confirm_dialog_message_with_name, turipName)
            } ?: stringResource(Res.string.invitation_confirm_dialog_message)

        TuripDialog(
            title = title,
            message = message,
            confirmText = stringResource(Res.string.invitation_confirm_dialog_confirm),
            dismissText = stringResource(Res.string.invitation_confirm_dialog_dismiss),
            onConfirmation = onConfirmEnterInvitedTurip,
            onDismissRequest = onCancelEnterInvitedTurip,
        )
    }

    uiState.dialogState?.let { dialogState ->
        when (dialogState) {
            is InvitationEntryDialogState.Failure -> {
                InvitationEntryFailureDialog(
                    dialogState = dialogState,
                    onRetry = onRetryResolveEntry,
                    onDismiss = onConfirmFailureDialog,
                )
            }

            is InvitationEntryDialogState.Invalid -> {
                InvitationEntryInvalidDialog(
                    dialogState = dialogState,
                    onConfirm = onResolveInvalidInvitationDialog,
                    onDismiss = onResolveInvalidInvitationDialog,
                )
            }
        }
    }
}

@Composable
private fun InvitationEntryLoading(
    turipName: String?,
    modifier: Modifier = Modifier,
) {
    val loadingInvitationTitle =
        turipName?.let { name ->
            stringResource(Res.string.invitation_loading_title_with_name, name)
        } ?: stringResource(Res.string.invitation_loading_title)

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.small),
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(TuripTheme.spacing.large))

            Text(
                text = loadingInvitationTitle,
                style = TuripTheme.typography.title1,
            )

            Text(
                text = stringResource(Res.string.invitation_loading_message),
                style = TuripTheme.typography.body1,
            )
        }
    }
}

@Composable
private fun InvitationEntryFailureDialog(
    dialogState: InvitationEntryDialogState.Failure,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
) {
    val message =
        if (dialogState.retryable) {
            when (dialogState.target) {
                InvalidInvitationTarget.Home -> stringResource(Res.string.invitation_failure_dialog_message_home)
                InvalidInvitationTarget.Login -> stringResource(Res.string.invitation_failure_dialog_message_login)
            }
        } else {
            when (dialogState.target) {
                InvalidInvitationTarget.Home -> stringResource(Res.string.invitation_failure_dialog_message_unhandled_home)
                InvalidInvitationTarget.Login -> stringResource(Res.string.invitation_failure_dialog_message_unhandled_login)
            }
        }
    val confirmText =
        if (dialogState.retryable) stringResource(Res.string.retry) else stringResource(Res.string.all_confirm)
    val dismissText =
        if (dialogState.retryable) {
            when (dialogState.target) {
                InvalidInvitationTarget.Home -> stringResource(Res.string.invitation_failure_dialog_dismiss_home)
                InvalidInvitationTarget.Login -> stringResource(Res.string.invitation_failure_dialog_dismiss_login)
            }
        } else {
            stringResource(Res.string.all_close_description)
        }

    TuripDialog(
        title = stringResource(Res.string.invitation_failure_dialog_title),
        message = message,
        confirmText = confirmText,
        dismissText = dismissText,
        onConfirmation = {
            if (dialogState.retryable) {
                onRetry()
            } else {
                onDismiss()
            }
        },
        onDismissRequest = onDismiss,
    )
}

@Composable
private fun InvitationEntryInvalidDialog(
    dialogState: InvitationEntryDialogState.Invalid,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val message =
        when (dialogState.target) {
            InvalidInvitationTarget.Home -> stringResource(Res.string.invitation_invalid_dialog_message_home)
            InvalidInvitationTarget.Login -> stringResource(Res.string.invitation_invalid_dialog_message_login)
        }
    TuripDialog(
        title = stringResource(Res.string.invitation_invalid_dialog_title),
        message = message,
        confirmText = stringResource(Res.string.all_confirm),
        dismissText = stringResource(Res.string.all_close_description),
        onConfirmation = onConfirm,
        onDismissRequest = onDismiss,
    )
}

@Immutable
private data class InvitationEntryUiState(
    val deepLinkUrl: String?,
    val invitationTuripId: Long?,
    val invitationTuripName: String?,
    val dialogState: InvitationEntryDialogState?,
)

@Immutable
private sealed interface InvitationEntryDialogState {
    @Immutable
    data class Invalid(
        val target: InvalidInvitationTarget,
    ) : InvitationEntryDialogState

    @Immutable
    data class Failure(
        val target: InvalidInvitationTarget,
        val retryable: Boolean,
    ) : InvitationEntryDialogState
}

private enum class InvalidInvitationTarget {
    Home,
    Login,
}

@Composable
@Preview(showBackground = true)
private fun InvitationEntryScreenPreview() {
    TuripTheme {
        InvitationEntryScreenContent(
            uiState =
                InvitationEntryUiState(
                    deepLinkUrl = "https://invite.turip.kro.kr?token=preview",
                    invitationTuripId = 1L,
                    invitationTuripName = "도쿄 여행",
                    dialogState = null,
                ),
            onConfirmEnterInvitedTurip = {},
            onCancelEnterInvitedTurip = {},
            onRetryResolveEntry = {},
            onConfirmFailureDialog = {},
            onResolveInvalidInvitationDialog = {},
        )
    }
}
