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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.on.turip.core.designsystem.component.TuripDialog
import com.on.turip.core.designsystem.snackbar.LocalSnackbarDelegate
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.feature.mypage.impl.component.MyPageAppBar
import com.on.turip.feature.mypage.impl.component.MyPageBookmarkSection
import com.on.turip.feature.mypage.impl.component.MyPageSettingsSection
import com.on.turip.feature.mypage.impl.component.ProfileSection
import com.on.turip.feature.mypage.impl.viewmodel.MyPageDialogState
import com.on.turip.feature.mypage.impl.viewmodel.MyPageEffect
import com.on.turip.feature.mypage.impl.viewmodel.MyPageIntent
import com.on.turip.feature.mypage.impl.viewmodel.MyPageViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MyPageScreen(
    onNavigateToAllBookmarks: () -> Unit,
    onNavigateToContent: (contentId: Long) -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: MyPageViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarDelegate = LocalSnackbarDelegate.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                MyPageEffect.NavigateToLogin -> onNavigateToLogin()
                is MyPageEffect.ShowRemoveFailedSnackbar -> {
                    snackbarDelegate.showSnackbar(
                        message = "북마크 삭제에 실패했어요",
                        actionLabel = "되돌리기",
                        onAction = { viewModel.onIntent(MyPageIntent.RollbackRemove(effect.contentId)) },
                        onDismiss = { viewModel.onIntent(MyPageIntent.RollbackRemove(effect.contentId)) },
                    )
                }
                MyPageEffect.ShowBookmarksLoadFailed ->
                    snackbarDelegate.showSnackbar(message = "북마크 목록을 불러오지 못했어요")
                MyPageEffect.ShowProfileLoadFailed ->
                    snackbarDelegate.showSnackbar(message = "프로필을 불러오지 못했어요")
                MyPageEffect.ShowLogoutError ->
                    snackbarDelegate.showSnackbar(message = "로그아웃에 실패했어요. 다시 시도해주세요")
                MyPageEffect.ShowWithdrawError ->
                    snackbarDelegate.showSnackbar(message = "회원 탈퇴에 실패했어요. 다시 시도해주세요")
                is MyPageEffect.NavigateToPrivacyPolicy -> { /* TODO: open URL in browser */ }
                is MyPageEffect.NavigateToInquiry -> { /* TODO: open email client */ }
            }
        }
    }

    uiState.dialogState?.let { dialog ->
        when (dialog) {
            MyPageDialogState.LogoutRequired -> TuripDialog(
                title = "로그아웃",
                message = "정말 로그아웃 하시겠어요?",
                confirmText = "로그아웃",
                dismissText = "취소",
                confirmButtonColor = TuripTheme.colors.primary,
                dismissButtonColor = TuripTheme.colors.gray02,
                onConfirmation = { viewModel.onIntent(MyPageIntent.ConfirmLogout) },
                onDismissRequest = { viewModel.onIntent(MyPageIntent.DismissDialog) },
            )
            MyPageDialogState.ConfirmWithdraw -> TuripDialog(
                title = "회원 탈퇴",
                message = "탈퇴하면 모든 데이터가 삭제돼요. 정말 탈퇴하시겠어요?",
                confirmText = "탈퇴",
                dismissText = "취소",
                confirmButtonColor = TuripTheme.colors.error,
                dismissButtonColor = TuripTheme.colors.gray02,
                onConfirmation = { viewModel.onIntent(MyPageIntent.ConfirmWithdraw) },
                onDismissRequest = { viewModel.onIntent(MyPageIntent.DismissDialog) },
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TuripTheme.colors.white)
            .systemBarsPadding(),
    ) {
        MyPageAppBar()
        LazyColumn(
            contentPadding = PaddingValues(
                top = TuripTheme.spacing.extraLarge,
                bottom = TuripTheme.spacing.medium,
            ),
            verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.extraExtraLarge),
        ) {
            item {
                ProfileSection(
                    profile = uiState.profile,
                    isLoading = uiState.isProfileLoading,
                    isError = uiState.isProfileError,
                    onRetry = { viewModel.onIntent(MyPageIntent.LoadProfile) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = TuripTheme.spacing.extraLarge),
                )
            }
            item {
                MyPageBookmarkSection(
                    bookmarks = uiState.bookmarks,
                    isLoading = uiState.isBookmarksLoading,
                    isError = uiState.isBookmarksError,
                    onViewAll = onNavigateToAllBookmarks,
                    onContentClick = onNavigateToContent,
                    onRemoveBookmark = { viewModel.onIntent(MyPageIntent.RemoveBookmark(it)) },
                    onRetry = { viewModel.onIntent(MyPageIntent.LoadBookmarks) },
                )
            }
            item {
                MyPageSettingsSection(
                    isLoggedIn = uiState.profile != null,
                    onInquiryClick = { viewModel.onIntent(MyPageIntent.ClickInquiry) },
                    onPrivacyPolicyClick = { viewModel.onIntent(MyPageIntent.ClickPrivacyPolicy) },
                    onLoginClick = onNavigateToLogin,
                    onLogoutClick = { viewModel.onIntent(MyPageIntent.ClickLogout) },
                    onWithdrawClick = { viewModel.onIntent(MyPageIntent.ClickWithdraw) },
                    modifier = Modifier.padding(horizontal = TuripTheme.spacing.medium),
                )
            }
        }
    }
}
