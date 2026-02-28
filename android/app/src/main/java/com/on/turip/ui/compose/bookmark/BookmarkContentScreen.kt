package com.on.turip.ui.compose.bookmark

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
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
import com.on.turip.ui.common.paging.PagingState
import com.on.turip.ui.compose.bookmark.component.BookmarkContentAppBar
import com.on.turip.ui.compose.bookmark.component.BookmarkContentItem
import com.on.turip.ui.compose.designsystem.component.ErrorScreen
import com.on.turip.ui.compose.designsystem.component.TuripSnackbar
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun BookmarkContentScreen(
    onNavigateToBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToContent: (contentId: Long) -> Unit,
    onBookmarkChanged: () -> Unit,
    viewModel: BookmarkContentViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { uiEffect: BookmarkContentUiEffect ->
            when (uiEffect) {
                BookmarkContentUiEffect.NavigateToLogin -> {
                    onNavigateToLogin()
                }

                BookmarkContentUiEffect.BookmarkRemoved -> {
                    onBookmarkChanged()
                }

                BookmarkContentUiEffect.ShowBookmarkRemoveFailed -> {
                    snackbarHostState.showSnackbarWithAction(
                        message = resources.getString(R.string.my_page_snackbar_bookmark_remove_failed),
                        actionLabel = resources.getString(R.string.my_page_snackbar_bookmark_remove_failed_action),
                        onAction = viewModel::refreshBookmarkContents,
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = { BookmarkContentAppBar(onBackClick = onNavigateToBack) },
        modifier =
            Modifier
                .fillMaxSize()
                .background(TuripTheme.colors.white)
                .systemBarsPadding(),
        snackbarHost = { TuripSnackbar(snackbarHostState = snackbarHostState) },
    ) { innerPadding ->
        BookmarkContentContent(
            uiState = uiState,
            onRetryClick = viewModel::refreshBookmarkContents,
            onContentClick = onNavigateToContent,
            onBookmarkClick = viewModel::removeBookmark,
            loadMoreContents = viewModel::loadMoreContents,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun BookmarkContentContent(
    uiState: BookmarkContentUiState,
    onRetryClick: () -> Unit,
    onContentClick: (contentId: Long) -> Unit,
    onBookmarkClick: (contentId: Long) -> Unit,
    loadMoreContents: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
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
                        loadMore = loadMoreContents,
                    )
                }
            }
        }
    }
}

@Composable
private fun BookmarkLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(60.dp),
            color = TuripTheme.colors.black,
        )
    }
}

@Composable
private fun BookmarkContentEmpty(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize(),
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
    loadMore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagingState: PagingState<BookmarkContent> = uiState.bookmarkContents
    val listState = rememberLazyListState()
    val threshold = 1
    val shouldLoadMore by remember {
        derivedStateOf {
            if (!pagingState.hasNext || pagingState.isAppending || pagingState.errorUiState != ErrorUiState.None ||
                pagingState.items.isEmpty()
            ) {
                return@derivedStateOf false
            }

            val layoutInfo = listState.layoutInfo
            val totalCount = layoutInfo.totalItemsCount
            val lastVisibleIndex =
                layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: return@derivedStateOf false
            val lastIndex = totalCount - 1

            lastVisibleIndex >= lastIndex - threshold
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { shouldLoadMore }
            .distinctUntilChanged()
            .filter { it }
            .collect { loadMore() }
    }

    val totalBookmarkCount =
        if (uiState.totalBookmarkCount != null) {
            stringResource(R.string.bookmark_content_count, uiState.totalBookmarkCount)
        } else {
            stringResource(R.string.bookmark_content_count_fail)
        }

    Column(modifier = modifier) {
        Text(
            text = totalBookmarkCount,
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
            state = listState,
            contentPadding = PaddingValues(TuripTheme.spacing.medium),
            modifier = Modifier.weight(1f),
        ) {
            itemsIndexed(
                items = pagingState.items,
                key = { _, item -> item.content.id },
            ) { index, content ->
                BookmarkContentItem(
                    content = content,
                    onContentClick = onContentClick,
                    onRemoveBookmark = onBookmarkClick,
                )

                if (index != pagingState.items.lastIndex) {
                    HorizontalDivider(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = TuripTheme.spacing.medium),
                        thickness = 1.dp,
                        color = TuripTheme.colors.gray01,
                    )
                }
            }

            if (pagingState.isAppending) {
                item {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(TuripTheme.spacing.large),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = TuripTheme.colors.black,
                        )
                    }
                }
            } else if (pagingState.errorUiState != ErrorUiState.None) {
                item {
                    LoadMoreError(onRetryClick = loadMore)
                }
            }
        }
    }
}

@Composable
private fun LoadMoreError(onRetryClick: () -> Unit) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = TuripTheme.spacing.large),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.bookmark_content_load_more_fail_title),
            style = TuripTheme.typography.info1,
            modifier = Modifier.weight(1f),
        )

        TextButton(onClick = onRetryClick) {
            Text(
                text = stringResource(R.string.retry),
                style = TuripTheme.typography.info2,
                color = TuripTheme.colors.gray04,
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
            loadMoreContents = { },
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
                    bookmarkContents =
                        PagingState(
                            items = persistentListOf(),
                            hasNext = false,
                            isAppending = false,
                            errorUiState = ErrorUiState.None,
                        ),
                    totalBookmarkCount = null,
                    errorUiState = ErrorUiState.None,
                ),
            onRetryClick = {},
            onContentClick = {},
            onBookmarkClick = {},
            loadMoreContents = { },
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
                    bookmarkContents =
                        PagingState(
                            items = persistentListOf(),
                            hasNext = false,
                            isAppending = false,
                            errorUiState = ErrorUiState.None,
                        ),
                    totalBookmarkCount = null,
                    errorUiState = ErrorUiState.Network,
                ),
            onRetryClick = {},
            onContentClick = {},
            onBookmarkClick = {},
            loadMoreContents = { },
        )
    }
}

@Preview(showBackground = true, name = "정상")
@Composable
private fun BookmarkContentSuccessPreview() {
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
        Column {
            BookmarkContentContent(
                uiState =
                    BookmarkContentUiState(
                        isLoading = false,
                        bookmarkContents =
                            PagingState(
                                items = contents,
                                hasNext = false,
                                isAppending = false,
                                errorUiState = ErrorUiState.None,
                            ),
                        totalBookmarkCount = 10,
                        errorUiState = ErrorUiState.None,
                    ),
                onRetryClick = {},
                onContentClick = {},
                onBookmarkClick = {},
                loadMoreContents = {},
            )
        }
    }
}
