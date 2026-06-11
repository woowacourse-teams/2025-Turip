package com.on.turip.feature.bookmark.impl

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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.all_close_description
import com.on.turip.core.designsystem.generated.resources.all_mascot_description
import com.on.turip.core.designsystem.generated.resources.bookmark_content_count
import com.on.turip.core.designsystem.generated.resources.bookmark_content_count_fail
import com.on.turip.core.designsystem.generated.resources.bookmark_content_load_more_fail_title
import com.on.turip.core.designsystem.generated.resources.bookmark_content_snackbar_bookmark_remove_failed
import com.on.turip.core.designsystem.generated.resources.mascot
import com.on.turip.core.designsystem.generated.resources.my_page_empty_bookmark_content
import com.on.turip.core.designsystem.generated.resources.retry
import com.on.turip.core.designsystem.snackbar.LocalSnackbarDelegate
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.model.bookmark.BookmarkContent
import com.on.turip.core.ui.component.ErrorScreen
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.core.ui.util.formatResource
import com.on.turip.feature.bookmark.impl.component.BookmarkContentListAppBar
import com.on.turip.feature.bookmark.impl.component.BookmarkContentListItem
import com.on.turip.feature.bookmark.impl.paging.PagingState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BookmarkContentListScreen(
    onBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToContent: (contentId: Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BookmarkContentListViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarDelegate = LocalSnackbarDelegate.current

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { uiEffect: BookmarkContentListUiEffect ->
            when (uiEffect) {
                BookmarkContentListUiEffect.NavigateToLogin -> {
                    onNavigateToLogin()
                }

                is BookmarkContentListUiEffect.ShowBookmarkRemoveFailedList -> {
                    snackbarDelegate.showSnackbar(
                        message = getString(Res.string.bookmark_content_snackbar_bookmark_remove_failed),
                        actionLabel = getString(Res.string.all_close_description),
                        onAction = { viewModel.rollbackBookmarkContentRemove(uiEffect.contentId) },
                        onDismiss = { viewModel.rollbackBookmarkContentRemove(uiEffect.contentId) },
                    )
                }
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
        BookmarkContentListAppBar(onBackClick = onBack)
        BookmarkContentListContent(
            uiState = uiState,
            onRetryClick = viewModel::refreshBookmarkContents,
            onContentClick = onNavigateToContent,
            onBookmarkClick = viewModel::removeBookmark,
            onLoadMore = viewModel::loadMoreContents,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun BookmarkContentListContent(
    uiState: BookmarkContentListUiState,
    onRetryClick: () -> Unit,
    onContentClick: (contentId: Long) -> Unit,
    onBookmarkClick: (contentId: Long) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                BookmarkContentListLoading()
            }

            uiState.errorUiState != ErrorUiState.None -> {
                ErrorScreen(
                    errorUiState = uiState.errorUiState,
                    onRetryClick = onRetryClick,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            uiState.isEmpty -> {
                BookmarkContentListEmpty()
            }

            else -> {
                BookmarkContentList(
                    uiState = uiState,
                    onContentClick = onContentClick,
                    onBookmarkClick = onBookmarkClick,
                    onLoadMore = onLoadMore,
                )
            }
        }
    }
}

@Composable
private fun BookmarkContentListLoading(modifier: Modifier = Modifier) {
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
private fun BookmarkContentListEmpty(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize(),
    ) {
        Spacer(modifier = Modifier.height(TuripTheme.spacing.huge))

        Image(
            painter = painterResource(Res.drawable.mascot),
            contentDescription = stringResource(Res.string.all_mascot_description),
            modifier = Modifier.size(70.dp),
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.large))

        Text(
            text = stringResource(Res.string.my_page_empty_bookmark_content),
            style = TuripTheme.typography.title2,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.extraHuge))
    }
}

@Composable
private fun BookmarkContentList(
    uiState: BookmarkContentListUiState,
    onContentClick: (contentId: Long) -> Unit,
    onBookmarkClick: (contentId: Long) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagingState: PagingState<BookmarkContent> = uiState.bookmarkContents
    val listState = rememberLazyListState()
    val threshold = 3
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
            .collect { onLoadMore() }
    }

    val totalBookmarkCount =
        if (uiState.totalBookmarkCount != null) {
            stringResource(Res.string.bookmark_content_count).formatResource(uiState.totalBookmarkCount)
        } else {
            stringResource(Res.string.bookmark_content_count_fail)
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
                key = { _, item -> item.bookmarkId },
            ) { index, content ->
                BookmarkContentListItem(
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
                    LoadMoreError(onRetryClick = onLoadMore)
                }
            }
        }
    }
}

@Composable
private fun LoadMoreError(
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = TuripTheme.spacing.extraSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.bookmark_content_load_more_fail_title),
            style = TuripTheme.typography.info1,
            modifier = Modifier.weight(1f),
        )

        TextButton(onClick = onRetryClick) {
            Text(
                text = stringResource(Res.string.retry),
                style = TuripTheme.typography.info2,
                color = TuripTheme.colors.gray04,
            )
        }
    }
}
