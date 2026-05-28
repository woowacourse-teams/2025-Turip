package com.on.turip.feature.bookmark.impl

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.snackbar.LocalSnackbarDelegate
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.feature.bookmark.impl.component.BookmarkAppBar
import com.on.turip.feature.bookmark.impl.component.BookmarkContentItem
import com.on.turip.feature.bookmark.impl.viewmodel.BookmarkEffect
import com.on.turip.feature.bookmark.impl.viewmodel.BookmarkIntent
import com.on.turip.feature.bookmark.impl.viewmodel.BookmarkState
import com.on.turip.feature.bookmark.impl.viewmodel.BookmarkViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BookmarkScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToTripDetail: (contentId: Long) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BookmarkViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarDelegate = LocalSnackbarDelegate.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is BookmarkEffect.NavigateToLogin -> onNavigateToLogin()
                is BookmarkEffect.ShowRemoveFailedSnackbar -> {
                    snackbarDelegate.showSnackbar(
                        message = "북마크 해제에 실패했습니다.",
                        actionLabel = "되돌리기",
                        onAction = {
                            viewModel.onIntent(BookmarkIntent.RollbackRemove(effect.contentId))
                        },
                    )
                }
            }
        }
    }

    BookmarkContent(
        state = uiState,
        onIntent = viewModel::onIntent,
        onItemClick = onNavigateToTripDetail,
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@Composable
private fun BookmarkContent(
    state: BookmarkState,
    onIntent: (BookmarkIntent) -> Unit,
    onItemClick: (contentId: Long) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }
            .distinctUntilChanged()
            .filter { layoutInfo ->
                val visibleItems = layoutInfo.visibleItemsInfo
                val totalItems = layoutInfo.totalItemsCount
                visibleItems.isNotEmpty() && visibleItems.last().index >= totalItems - 3
            }
            .collectLatest {
                onIntent(BookmarkIntent.LoadMore)
            }
    }

    Column(modifier = modifier.fillMaxSize()) {
        BookmarkAppBar(onBackClick = onBackClick)

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = TuripTheme.colors.primary,
                    )
                }

                state.isError -> {
                    BookmarkErrorView(
                        onRetry = { onIntent(BookmarkIntent.Refresh) },
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                state.isEmpty -> {
                    BookmarkEmptyView(
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(
                            items = state.contents,
                            key = { it.id },
                        ) { content ->
                            BookmarkContentItem(
                                content = content,
                                onItemClick = onItemClick,
                                onRemoveBookmark = { onIntent(BookmarkIntent.RemoveBookmark(it)) },
                            )
                            HorizontalDivider(
                                color = TuripTheme.colors.gray01,
                                thickness = 1.dp,
                            )
                        }

                        if (state.isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularProgressIndicator(
                                        color = TuripTheme.colors.primary,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BookmarkEmptyView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "북마크한 콘텐츠가 없습니다.",
            style = TuripTheme.typography.body2,
            color = TuripTheme.colors.gray03,
        )
    }
}

@Composable
private fun BookmarkErrorView(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "콘텐츠를 불러오지 못했습니다.",
            style = TuripTheme.typography.body2,
            color = TuripTheme.colors.gray03,
        )
        Spacer(modifier = Modifier.height(8.dp))
        androidx.compose.material3.TextButton(onClick = onRetry) {
            Text(
                text = "다시 시도",
                style = TuripTheme.typography.body2,
                color = TuripTheme.colors.primary,
            )
        }
    }
}
