package com.on.turip.ui.compose.bookmarks

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.on.turip.R
import com.on.turip.domain.bookmark.BookmarkContent
import com.on.turip.domain.content.Content
import com.on.turip.domain.content.video.VideoData
import com.on.turip.domain.creator.Creator
import com.on.turip.domain.region.City
import com.on.turip.domain.trip.TripDuration
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.extensions.showSnackbarWithAction
import com.on.turip.ui.compose.bookmarks.component.BookmarkContentAppBar
import com.on.turip.ui.compose.bookmarks.component.BookmarkContentItem
import com.on.turip.ui.compose.designsystem.component.ErrorScreen
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import kotlinx.collections.immutable.persistentListOf

@Composable
fun BookmarkContentScreen(
    navigateToBack: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToContent: (contentId: Long) -> Unit,
    viewModel: BookmarkContentViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { uiEffect: BookmarkContentUiEffect ->
            when (uiEffect) {
                BookmarkContentUiEffect.NavigateToLogin -> {
                    navigateToLogin()
                }

                BookmarkContentUiEffect.ShowBookmarkRemoveFailed -> {
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
        topBar = { BookmarkContentAppBar(onBackClick = navigateToBack) },
        modifier =
            Modifier
                .fillMaxSize()
                .background(TuripTheme.colors.white)
                .systemBarsPadding(),
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
        ) {
            BookmarkContentContent(
                uiState = uiState,
                onRetryClick = viewModel::loadBookmarkContents,
                onContentClick = navigateToContent,
                onBookmarkClick = viewModel::removeBookmark,
            )
        }
    }
}

@Composable
private fun BookmarkContentContent(
    uiState: BookmarkContentUiState,
    onRetryClick: () -> Unit,
    onContentClick: (contentId: Long) -> Unit,
    onBookmarkClick: (contentId: Long) -> Unit,
) {
    when {
        uiState.isLoading -> {
            BookmarkLoading()
        }

        uiState.errorUiState != ErrorUiState.None -> {
            ErrorScreen(
                errorUiState = uiState.errorUiState,
                onRetryClick = onRetryClick,
                modifier = Modifier.fillMaxSize(),
            )
        }

        else -> {
            if (uiState.isEmpty) {
                BookmarkContentEmpty()
            } else {
                BookmarkContents(
                    uiState = uiState,
                    onContentClick = onContentClick,
                    onBookmarkClick = onBookmarkClick,
                )
            }
        }
    }
}

@Composable
private fun BookmarkLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(60.dp),
            color = TuripTheme.colors.black,
        )
    }
}

@Composable
private fun BookmarkContentEmpty() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize(),
    ) {
        Spacer(modifier = Modifier.height(TuripTheme.spacing.huge))

        Image(
            painter = painterResource(R.drawable.mascot),
            contentDescription = stringResource(R.string.all_mascot_description),
            modifier = Modifier.size(70.dp),
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.large))

        Text(
            text = stringResource(R.string.my_page_empty_bookmark_content),
            style = TuripTheme.typography.title2,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.extraHuge))
    }
}

@Composable
private fun BookmarkContents(
    uiState: BookmarkContentUiState,
    onContentClick: (contentId: Long) -> Unit,
    onBookmarkClick: (contentId: Long) -> Unit,
) {
    Text(
        text =
            stringResource(
                R.string.bookmark_content_count,
                uiState.bookmarkContents.size,
            ),
        textAlign = TextAlign.End,
        style = TuripTheme.typography.info2,
        color = TuripTheme.colors.gray03,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = TuripTheme.spacing.medium)
                .padding(end = TuripTheme.spacing.large),
    )

    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        thickness = 5.dp,
        color = TuripTheme.colors.gray01,
    )

    LazyColumn(
        contentPadding = PaddingValues(TuripTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.medium),
    ) {
        itemsIndexed(
            items = uiState.bookmarkContents,
            key = { _, item -> item.content.id },
        ) { index, content ->
            BookmarkContentItem(
                content = content,
                showDivider = index != uiState.bookmarkContents.lastIndex,
                onContentClick = onContentClick,
                onRemoveBookmark = onBookmarkClick,
            )
        }
    }
}

@Preview(showBackground = true, name = "로딩")
@Composable
private fun BookmarkContentLoadingPreview() {
    TuripTheme {
        BookmarkContentContent(
            uiState = BookmarkContentUiState.Idle,
            onRetryClick = {},
            onContentClick = {},
            onBookmarkClick = {},
        )
    }
}

@Preview(showBackground = true, name = "북마크 콘텐츠 비어 있는 경우")
@Composable
private fun BookmarkContentEmptyPreview() {
    TuripTheme {
        BookmarkContentContent(
            uiState =
                BookmarkContentUiState(
                    isLoading = false,
                    bookmarkContents = persistentListOf(),
                    errorUiState = ErrorUiState.None,
                ),
            onRetryClick = {},
            onContentClick = {},
            onBookmarkClick = {},
        )
    }
}

@Preview(showBackground = true, name = "에러")
@Composable
private fun BookmarkContentErrorPreview() {
    TuripTheme {
        BookmarkContentContent(
            uiState =
                BookmarkContentUiState(
                    isLoading = false,
                    bookmarkContents = persistentListOf(),
                    errorUiState = ErrorUiState.Network,
                ),
            onRetryClick = {},
            onContentClick = {},
            onBookmarkClick = {},
        )
    }
}

@Preview(showBackground = true, name = "정상")
@Composable
private fun BookmarkContentErrorSuccess() {
    val contents =
        persistentListOf(
            BookmarkContent(
                content =
                    Content(
                        1L,
                        Creator(1L, "채널명", ""),
                        VideoData("콘텐츠 제목이 길면 ...으로 표시되는 것을 확인 ㅇㅇㅇ", "thumbnail", "2026-01-12"),
                        City("대구"),
                        true,
                    ),
                tripDuration = TripDuration(1, 2),
                tripPlaceCount = 2,
            ),
            BookmarkContent(
                content =
                    Content(
                        2L,
                        Creator(1L, "채널명이 길어지는 경우 채널명이 길어지는 경우 채널명이 길어지는 경우 채널명이 길어지는 경우 ", ""),
                        VideoData("콘텐츠 제목", "thumbnail", "2025-01-12"),
                        City("대구"),
                        true,
                    ),
                tripDuration = TripDuration(0, 1),
                tripPlaceCount = 2,
            ),
        )
    TuripTheme {
        BookmarkContentContent(
            uiState =
                BookmarkContentUiState(
                    isLoading = false,
                    bookmarkContents = contents,
                    errorUiState = ErrorUiState.None,
                ),
            onRetryClick = {},
            onContentClick = {},
            onBookmarkClick = {},
        )
    }
}
