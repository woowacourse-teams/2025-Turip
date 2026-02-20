package com.on.turip.ui.compose.mypage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.on.turip.R
import com.on.turip.ui.common.extensions.showSnackbarWithAction
import com.on.turip.ui.compose.designsystem.component.TuripSnackbar
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.mypage.component.BookmarkedContentSection
import com.on.turip.ui.compose.mypage.component.MyPageAppBar
import com.on.turip.ui.compose.mypage.component.MyPageSettingsSection
import com.on.turip.ui.compose.mypage.component.ProfileSection

@Composable
fun MyPageScreen(
    navigateToAllBookmarkContents: () -> Unit,
    navigateToContent: (contentId: Long) -> Unit,
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
            }
        }
    }

    Scaffold(
        topBar = { MyPageAppBar() },
        snackbarHost = { TuripSnackbar(snackbarHostState = snackbarHostState) },
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
                    nickname = "닉네임 넣어주기 필요 123123123123123123123123123123123123123123",
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
                    onViewAllContentClick = navigateToAllBookmarkContents,
                    onContentClick = navigateToContent,
                    onRemoveBookmark = viewModel::removeBookmark,
                    onRetry = viewModel::loadBookmarkContents,
                )
            }

            item {
                MyPageSettingsSection(
                    onInquiryClick = { },
                    onPrivacyPolicyClick = { },
                    onLoginClick = { },
                    onLogoutClick = { },
                    onWithdrawClick = { },
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
        MyPageScreen(
            navigateToAllBookmarkContents = {},
            navigateToContent = {},
        )
    }
}
