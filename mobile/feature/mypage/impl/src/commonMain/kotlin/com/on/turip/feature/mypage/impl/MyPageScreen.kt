package com.on.turip.feature.mypage.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.on.turip.core.designsystem.component.TuripDialog
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.all_close_description
import com.on.turip.core.designsystem.generated.resources.my_page_logout
import com.on.turip.core.designsystem.generated.resources.my_page_logout_dialog_confirm
import com.on.turip.core.designsystem.generated.resources.my_page_logout_dialog_dismiss
import com.on.turip.core.designsystem.generated.resources.my_page_logout_dialog_message
import com.on.turip.core.designsystem.generated.resources.my_page_snackbar_bookmark_remove_failed
import com.on.turip.core.designsystem.generated.resources.my_page_snackbar_bookmarks_load_failed
import com.on.turip.core.designsystem.generated.resources.my_page_snackbar_profile_load_failed
import com.on.turip.core.designsystem.generated.resources.my_page_withdraw
import com.on.turip.core.designsystem.generated.resources.my_page_withdraw_dialog_confirm
import com.on.turip.core.designsystem.generated.resources.my_page_withdraw_dialog_dismiss
import com.on.turip.core.designsystem.generated.resources.my_page_withdraw_dialog_message
import com.on.turip.core.designsystem.snackbar.LocalSnackbarDelegate
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.domain.session.SessionState
import com.on.turip.core.model.bookmark.BookmarkContent
import com.on.turip.core.ui.error.toUiModel
import com.on.turip.feature.mypage.impl.component.MyPageAppBar
import com.on.turip.feature.mypage.impl.component.MyPageBookmarkContentSection
import com.on.turip.feature.mypage.impl.component.MyPageSettingsSection
import com.on.turip.feature.mypage.impl.component.ProfileSection
import com.on.turip.feature.mypage.impl.model.InquiryMail
import com.on.turip.feature.mypage.impl.model.ProfileModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MyPageScreen(
    onNavigateToAllBookmarkContents: () -> Unit,
    onNavigateToContent: (contentId: Long) -> Unit,
    onNavigateToInquiry: (mail: InquiryMail) -> Unit,
    onNavigateToPrivacyPolicy: (url: String) -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyPageViewModel = koinViewModel(),
) {
    val uiState: MyPageUiState by viewModel.uiState.collectAsState()
    val sessionState: SessionState by viewModel.sessionState.collectAsState()

    val snackbarDelegate = LocalSnackbarDelegate.current

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collectLatest { uiEffect: MyPageUiEffect ->
            when (uiEffect) {
                is MyPageUiEffect.ShowBookmarkRemoveFailed -> {
                    snackbarDelegate.showSnackbar(
                        message = getString(Res.string.my_page_snackbar_bookmark_remove_failed),
                        actionLabel = getString(Res.string.all_close_description),
                        onAction = { viewModel.rollbackBookmarkContentRemove(uiEffect.contentId) },
                        onDismiss = { viewModel.rollbackBookmarkContentRemove(uiEffect.contentId) },
                    )
                }

                MyPageUiEffect.ShowBookmarksLoadFailed -> {
                    snackbarDelegate.showSnackbar(
                        message = getString(Res.string.my_page_snackbar_bookmarks_load_failed),
                        actionLabel = getString(Res.string.all_close_description),
                        duration = SnackbarDuration.Short,
                    )
                }

                MyPageUiEffect.ShowProfileLoadFailed -> {
                    snackbarDelegate.showSnackbar(
                        message = getString(Res.string.my_page_snackbar_profile_load_failed),
                        actionLabel = getString(Res.string.all_close_description),
                        duration = SnackbarDuration.Short,
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
                    val errorUiModel = uiEffect.errorUiState.toUiModel() ?: return@collectLatest
                    snackbarDelegate.showSnackbar(
                        message = getString(errorUiModel.titleRes),
                        actionLabel = getString(errorUiModel.retryTextRes),
                        onAction = { viewModel.handleErrorRetryRequest(uiEffect.retryAction) },
                    )
                }
            }
        }
    }

    MyPageScreenContent(
        uiState = uiState,
        sessionState = sessionState,
        onNavigateToAllBookmarkContents = onNavigateToAllBookmarkContents,
        onNavigateToContent = onNavigateToContent,
        onRemoveBookmark = { contentId: Long ->
            viewModel.removeBookmark(contentId)
        },
        onProfileRetry = {
            viewModel.loadProfile(isRetry = true)
        },
        onBookmarkRetry = {
            viewModel.loadBookmarkContents(isRetry = true)
        },
        onInquiryClick = viewModel::loadInquiryMail,
        onPrivacyPolicyClick = viewModel::loadPrivacyPolicy,
        onLoginClick = onNavigateToLogin,
        onLogoutClick = viewModel::loadLogoutDialog,
        onWithdrawClick = viewModel::loadWithdrawDialog,
        onDialogConfirmLogout = viewModel::confirmLogout,
        onDialogConfirmWithdraw = viewModel::confirmWithdraw,
        onDialogDismiss = viewModel::dismissDialog,
        modifier = modifier,
    )
}

@Composable
private fun MyPageScreenContent(
    uiState: MyPageUiState,
    sessionState: SessionState,
    onNavigateToAllBookmarkContents: () -> Unit,
    onNavigateToContent: (contentId: Long) -> Unit,
    onRemoveBookmark: (contentId: Long) -> Unit,
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
    modifier: Modifier = Modifier,
) {
    uiState.dialogState?.let { dialogState: MyPageDialogState ->
        when (dialogState) {
            MyPageDialogState.LogoutRequired -> {
                TuripDialog(
                    title = stringResource(Res.string.my_page_logout),
                    message = stringResource(Res.string.my_page_logout_dialog_message),
                    confirmText = stringResource(Res.string.my_page_logout_dialog_confirm),
                    dismissText = stringResource(Res.string.my_page_logout_dialog_dismiss),
                    confirmButtonColor = TuripTheme.colors.primary,
                    dismissButtonColor = TuripTheme.colors.gray02,
                    onConfirmation = onDialogConfirmLogout,
                    onDismissRequest = onDialogDismiss,
                )
            }

            MyPageDialogState.ConfirmWithdraw -> {
                TuripDialog(
                    title = stringResource(Res.string.my_page_withdraw),
                    message = stringResource(Res.string.my_page_withdraw_dialog_message),
                    confirmText = stringResource(Res.string.my_page_withdraw_dialog_confirm),
                    dismissText = stringResource(Res.string.my_page_withdraw_dialog_dismiss),
                    confirmButtonColor = TuripTheme.colors.error,
                    dismissButtonColor = TuripTheme.colors.gray02,
                    onConfirmation = onDialogConfirmWithdraw,
                    onDismissRequest = onDialogDismiss,
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
        MyPageAppBar()
        LazyColumn(
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
                MyPageBookmarkContentSection(
                    state = uiState.bookmarkContentState,
                    onViewAllContentClick = onNavigateToAllBookmarkContents,
                    onContentClick = onNavigateToContent,
                    onRemoveBookmark = onRemoveBookmark,
                    onRetry = onBookmarkRetry,
                )
            }

            item {
                MyPageSettingsSection(
                    sessionState = sessionState,
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
