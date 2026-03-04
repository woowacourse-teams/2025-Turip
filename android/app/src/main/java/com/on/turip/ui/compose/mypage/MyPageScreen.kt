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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.on.turip.R
import com.on.turip.domain.bookmark.BookmarkContent
import com.on.turip.domain.content.Content
import com.on.turip.domain.content.video.VideoData
import com.on.turip.domain.creator.Creator
import com.on.turip.domain.region.City
import com.on.turip.domain.trip.TripDuration
import com.on.turip.ui.common.error.toUiModel
import com.on.turip.ui.common.extensions.dismissAndExecute
import com.on.turip.ui.common.extensions.showSnackbarWithAction
import com.on.turip.ui.compose.designsystem.component.TuripDialog
import com.on.turip.ui.compose.designsystem.component.TuripSnackbar
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.mypage.component.MyPageAppBar
import com.on.turip.ui.compose.mypage.component.MyPageBookmarkContentSection
import com.on.turip.ui.compose.mypage.component.MyPageSettingsSection
import com.on.turip.ui.compose.mypage.component.ProfileSection
import com.on.turip.ui.compose.mypage.model.InquiryMail
import com.on.turip.ui.compose.mypage.model.ProfileModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

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
                is MyPageUiEffect.ShowBookmarkRemoveFailed -> {
                    snackbarHostState.showSnackbarWithAction(
                        message = resources.getString(R.string.my_page_snackbar_bookmark_remove_failed),
                        actionLabel = resources.getString(R.string.all_close_description),
                        onAction = { viewModel.rollbackBookmarkContentRemove(uiEffect.contentId) },
                        onDismiss = { viewModel.rollbackBookmarkContentRemove(uiEffect.contentId) },
                    )
                }

                MyPageUiEffect.ShowBookmarksLoadFailed -> {
                    snackbarHostState.showSnackbar(
                        message = resources.getString(R.string.my_page_snackbar_bookmarks_load_failed),
                        actionLabel = resources.getString(R.string.all_close_description),
                        duration = SnackbarDuration.Short,
                    )
                }

                MyPageUiEffect.ShowProfileLoadFailed -> {
                    snackbarHostState.showSnackbar(
                        message = resources.getString(R.string.my_page_snackbar_profile_load_failed),
                        actionLabel = resources.getString(R.string.all_close_description),
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
        onRemoveBookmark = { contentId: Long ->
            snackbarHostState.dismissAndExecute { viewModel.removeBookmark(contentId) }
        },
        onProfileRetry = { snackbarHostState.dismissAndExecute { viewModel.loadProfile(isRetry = true) } },
        onBookmarkRetry = {
            snackbarHostState.dismissAndExecute { viewModel.loadBookmarkContents(isRetry = true) }
        },
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
) {
    uiState.dialogState?.let { dialogState: MyPageDialogState ->
        when (dialogState) {
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
        }
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

private class MyPageUiStatePreviewProvider : PreviewParameterProvider<MyPageUiState> {
    override val values: Sequence<MyPageUiState> =
        sequenceOf(
            // 둘 다 성공
            MyPageUiState.Idle.copy(
                profileState =
                    MyPageSectionState.Success(
                        ProfileModel(
                            1L,
                            "닉네임은 최대 2줄 까지 가능하도록 보여지고 있어요 닉네임은 최대 2줄 까지 가능하도록 보여지고 있어요",
                            null,
                        ),
                    ),
                bookmarkContentState = MyPageSectionState.Success(previewBookmarkContents()),
            ),
            // 프로필 & 북마크 에러
            MyPageUiState.Idle.copy(
                profileState = MyPageSectionState.Error,
                bookmarkContentState = MyPageSectionState.Error,
            ),
            // 북마크 로딩
            MyPageUiState.Idle.copy(
                bookmarkContentState = MyPageSectionState.Loading,
            ),
            // 로그아웃 다이얼로그
            MyPageUiState.Idle.copy(dialogState = MyPageDialogState.LogoutRequired),
            // 회원 탈퇴 다이얼로그
            MyPageUiState.Idle.copy(dialogState = MyPageDialogState.ConfirmWithdraw),
        )
}

private fun previewBookmarkContents(): ImmutableList<BookmarkContent> =
    persistentListOf(
        BookmarkContent(
            bookmarkId = 1L,
            content =
                Content(
                    id = 1L,
                    creator = Creator(1L, "채널명", ""),
                    videoData =
                        VideoData(
                            title = "콘텐츠 제목이 길면 말줄임 처리되는지 확인합니다",
                            url = "thumbnail",
                            uploadedDate = "2026-02-23",
                        ),
                    city = City("대구"),
                    isBookmarked = true,
                ),
            tripDuration = TripDuration(1, 2),
            tripPlaceCount = 3,
            createdAt = "",
        ),
        BookmarkContent(
            bookmarkId = 2L,
            content =
                Content(
                    id = 2L,
                    creator = Creator(2L, "긴 채널명 긴 채널명 긴 채널명", ""),
                    videoData =
                        VideoData(
                            title = "짧은 제목",
                            url = "thumbnail",
                            uploadedDate = "2026-02-10",
                        ),
                    city = City("제주"),
                    isBookmarked = true,
                ),
            tripDuration = TripDuration(0, 1),
            tripPlaceCount = 5,
            createdAt = "",
        ),
    )

@Preview(showBackground = true)
@Composable
private fun MyPageScreenPreview(
    @PreviewParameter(MyPageUiStatePreviewProvider::class) uiState: MyPageUiState,
) {
    TuripTheme {
        MyPageScreenContent(
            uiState = uiState,
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
