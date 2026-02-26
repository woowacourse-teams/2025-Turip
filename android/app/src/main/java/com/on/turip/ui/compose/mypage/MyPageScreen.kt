package com.on.turip.ui.compose.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.on.turip.R
import com.on.turip.ui.common.error.toUiModel
import com.on.turip.ui.common.extensions.showSnackbarWithAction
import com.on.turip.ui.compose.designsystem.component.TuripDialog
import com.on.turip.ui.compose.designsystem.component.TuripSnackbar
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.mypage.component.BookmarkedContentSection
import com.on.turip.ui.compose.mypage.component.MyPageAppBar
import com.on.turip.ui.compose.mypage.component.MyPageSettingsSection
import com.on.turip.ui.compose.mypage.component.ProfileSection
import com.on.turip.ui.compose.mypage.model.InquiryMail

@Composable
fun MyPageScreen(
    onNavigateToAllBookmarkContents: () -> Unit,
    onNavigateToContent: (contentId: Long) -> Unit,
    onNavigateToInquiry: (mail: InquiryMail) -> Unit,
    onNavigateToPrivacyPolicy: (url: String) -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: MyPageViewModel = hiltViewModel(),
) {
    val uiState: MyPageUiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { uiEffect: MyPageUiEffect ->
            when (uiEffect) {
                MyPageUiEffect.ShowBookmarkRemoveFailed -> {
                    snackbarHostState.showSnackbarWithAction(
                        message = resources.getString(R.string.my_page_snackbar_bookmark_remove_failed),
                        actionLabel = resources.getString(R.string.my_page_snackbar_bookmark_remove_failed_action),
                        onAction = viewModel::loadBookmarkContents,
                    )
                }

                is MyPageUiEffect.NavigateToInquiry -> {
                    onNavigateToInquiry(uiEffect.mail)
                }

                MyPageUiEffect.NavigateToLogin -> {
                    onNavigateToLogin()
                }

                is MyPageUiEffect.NavigateToPrivacyPolicy -> {
                    onNavigateToPrivacyPolicy(uiEffect.url)
                }

                is MyPageUiEffect.ShowError -> {
                    val errorUiModel = uiEffect.errorUiState.toUiModel() ?: return@collect
                    snackbarHostState.showSnackbarWithAction(
                        message = resources.getString(errorUiModel.titleRes),
                        actionLabel = resources.getString(errorUiModel.retryTextRes),
                        onAction = { viewModel.handleErrorRetryRequest(uiEffect.retryAction) },
                    )
                }
            }
        }
    }

    MyPageScreenContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onNavigateToAllBookmarkContents = onNavigateToAllBookmarkContents,
        onNavigateToContent = onNavigateToContent,
        onRemoveBookmark = viewModel::removeBookmark,
        onProfileRetry = viewModel::loadProfile,
        onBookmarkRetry = viewModel::loadBookmarkContents,
        onInquiryClick = viewModel::loadInquiryMail,
        onPrivacyPolicyClick = viewModel::loadPrivacyPolicy,
        onLoginClick = onNavigateToLogin,
        onLogoutClick = viewModel::loadLogoutDialog,
        onWithdrawClick = viewModel::loadWithdrawDialog,
        onDialogConfirmLogout = viewModel::confirmLogout,
        onDialogConfirmWithdraw = viewModel::confirmWithdraw,
        onDialogDismiss = viewModel::dismissDialog,
    )
}

@Composable
private fun MyPageScreenContent(
    uiState: MyPageUiState,
    snackbarHostState: SnackbarHostState,
    onNavigateToAllBookmarkContents: () -> Unit,
    onNavigateToContent: (contentId: Long) -> Unit,
    onRemoveBookmark: (Long) -> Unit,
    onProfileRetry: () -> Unit,
    onBookmarkRetry: () -> Unit,
    onInquiryClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    onDialogConfirmLogout: () -> Unit,
    onDialogConfirmWithdraw: () -> Unit,
    onDialogDismiss: () -> Unit,
) {
    when (uiState.dialogState) {
        MyPageDialogState.LogoutRequired -> {
            TuripDialog(
                title = stringResource(R.string.my_page_logout),
                message = stringResource(R.string.my_page_logout_dialog_message),
                confirmText = stringResource(R.string.my_page_logout_dialog_confirm),
                dismissText = stringResource(R.string.my_page_logout_dialog_dismiss),
                confirmButtonColor = TuripTheme.colors.primary,
                dismissButtonColor = TuripTheme.colors.gray02,
                onConfirmation = onDialogConfirmLogout,
                onDismissRequest = onDialogDismiss,
            )
        }

        MyPageDialogState.ConfirmWithdraw -> {
            TuripDialog(
                title = stringResource(R.string.my_page_withdraw),
                message = stringResource(R.string.my_page_withdraw_dialog_message),
                confirmText = stringResource(R.string.my_page_withdraw_dialog_confirm),
                dismissText = stringResource(R.string.my_page_withdraw_dialog_dismiss),
                confirmButtonColor = TuripTheme.colors.error,
                dismissButtonColor = TuripTheme.colors.gray02,
                onConfirmation = onDialogConfirmWithdraw,
                onDismissRequest = onDialogDismiss,
            )
        }

        null -> {}
    }

    Scaffold(
        topBar = { MyPageAppBar() },
        snackbarHost = { TuripSnackbar(snackbarHostState = snackbarHostState) },
        modifier =
            Modifier
                .fillMaxSize()
                .background(TuripTheme.colors.white)
                .systemBarsPadding(),
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding =
                PaddingValues(
                    top = TuripTheme.spacing.extraLarge,
                    bottom = TuripTheme.spacing.medium,
                ),
            verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.extraExtraLarge),
        ) {
            item {
                ProfileSection(
                    state = uiState.profileState,
                    onRetry = onProfileRetry,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = TuripTheme.spacing.extraLarge,
                                vertical = TuripTheme.spacing.small,
                            ),
                )
            }
            item {
                BookmarkedContentSection(
                    state = uiState.bookmarkContentState,
                    onViewAllContentClick = onNavigateToAllBookmarkContents,
                    onContentClick = onNavigateToContent,
                    onRemoveBookmark = onRemoveBookmark,
                    onRetry = onBookmarkRetry,
                )
            }

            item {
                MyPageSettingsSection(
                    onInquiryClick = onInquiryClick,
                    onPrivacyPolicyClick = onPrivacyPolicyClick,
                    onLoginClick = onLoginClick,
                    onLogoutClick = onLogoutClick,
                    onWithdrawClick = onWithdrawClick,
                    modifier = Modifier.padding(horizontal = TuripTheme.spacing.medium),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MyPageScreenPreview() {
    TuripTheme {
        MyPageScreenContent(
            uiState = MyPageUiState.Idle,
            snackbarHostState = remember { SnackbarHostState() },
            onNavigateToAllBookmarkContents = {},
            onNavigateToContent = {},
            onRemoveBookmark = {},
            onProfileRetry = {},
            onBookmarkRetry = {},
            onInquiryClick = {},
            onPrivacyPolicyClick = {},
            onLoginClick = {},
            onLogoutClick = {},
            onWithdrawClick = {},
            onDialogConfirmLogout = {},
            onDialogConfirmWithdraw = {},
            onDialogDismiss = {},
        )
    }
}
