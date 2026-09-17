package com.on.turip.feature.article.impl

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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.on.turip.core.designsystem.component.TuripLoadingIndicator
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.all_mascot_description
import com.on.turip.core.designsystem.generated.resources.article_list_empty
import com.on.turip.core.designsystem.generated.resources.article_list_load_more_fail_title
import com.on.turip.core.designsystem.generated.resources.mascot
import com.on.turip.core.designsystem.generated.resources.retry
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.ui.component.ErrorScreen
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.feature.article.impl.component.ArticleListAppBar
import com.on.turip.feature.article.impl.component.ArticleListItem
import com.on.turip.feature.article.impl.component.previewArticle
import com.on.turip.feature.article.impl.model.ArticleUiModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * 리스트 끝에서 이 개수만큼 남았을 때 다음 페이지를 미리 요청한다.
 */
private const val LOAD_MORE_THRESHOLD = 3

@Composable
fun ArticleListScreen(
    onBackClick: () -> Unit,
    onNavigateToLoginScreen: () -> Unit,
    onArticleClick: (articleId: Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ArticleListViewModel = koinViewModel(),
) {
    val uiState: ArticleListState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect: ArticleListEffect ->
            when (effect) {
                ArticleListEffect.NavigateToLogin -> onNavigateToLoginScreen()
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
        ArticleListAppBar(onBackClick = onBackClick)
        ArticleListContent(
            uiState = uiState,
            onIntent = viewModel::onIntent,
            onArticleClick = onArticleClick,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun ArticleListContent(
    uiState: ArticleListState,
    onIntent: (ArticleListIntent) -> Unit,
    onArticleClick: (articleId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                ArticleListLoading()
            }

            uiState.shouldShowErrorScreen -> {
                ErrorScreen(
                    errorUiState = uiState.errorUiState,
                    onRetryClick = { onIntent(ArticleListIntent.Retry) },
                    modifier = Modifier.fillMaxSize(),
                )
            }

            uiState.shouldShowEmptyView -> {
                ArticleListEmpty()
            }

            else -> {
                ArticleList(
                    uiState = uiState,
                    onArticleClick = onArticleClick,
                    onLoadMore = { onIntent(ArticleListIntent.LoadNextPage) },
                )
            }
        }
    }
}

@Composable
private fun ArticleListLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        TuripLoadingIndicator()
    }
}

@Composable
private fun ArticleListEmpty(modifier: Modifier = Modifier) {
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
            text = stringResource(Res.string.article_list_empty),
            style = TuripTheme.typography.title2,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.extraHuge))
    }
}

@Composable
private fun ArticleList(
    uiState: ArticleListState,
    onArticleClick: (articleId: Long) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState: LazyListState = rememberLazyListState()
    val shouldLoadMore: Boolean by remember(uiState.canLoadMore) {
        derivedStateOf {
            if (!uiState.canLoadMore) return@derivedStateOf false

            val layoutInfo = listState.layoutInfo
            val lastVisibleIndex: Int =
                layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: return@derivedStateOf false

            lastVisibleIndex >= layoutInfo.totalItemsCount - 1 - LOAD_MORE_THRESHOLD
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { shouldLoadMore }
            .distinctUntilChanged()
            .filter { it }
            .collect { onLoadMore() }
    }

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(TuripTheme.spacing.medium),
        modifier = modifier.fillMaxSize(),
    ) {
        itemsIndexed(
            items = uiState.articles,
            key = { _, article -> article.id },
        ) { index: Int, article: ArticleUiModel ->
            ArticleListItem(
                article = article,
                onClick = { onArticleClick(article.id) },
            )

            if (index != uiState.articles.lastIndex) {
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

        if (uiState.isAppending) {
            item(key = "appending") {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(TuripTheme.spacing.large),
                    contentAlignment = Alignment.Center,
                ) {
                    TuripLoadingIndicator(size = 24.dp)
                }
            }
        } else if (uiState.appendErrorUiState != ErrorUiState.None) {
            item(key = "append_error") {
                LoadMoreError(onRetryClick = onLoadMore)
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
            text = stringResource(Res.string.article_list_load_more_fail_title),
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

@Preview(showBackground = true)
@Composable
private fun ArticleListContentPreview() {
    TuripTheme {
        ArticleListContent(
            uiState =
                ArticleListState(
                    articles =
                        persistentListOf(
                            previewArticle(),
                            previewArticle().copy(id = 2L, authorName = null),
                        ),
                    isLoading = false,
                    isFetched = true,
                ),
            onIntent = {},
            onArticleClick = {},
        )
    }
}

@Preview(showBackground = true, name = "빈 목록")
@Composable
private fun ArticleListEmptyPreview() {
    TuripTheme {
        ArticleListContent(
            uiState = ArticleListState(isLoading = false, isFetched = true),
            onIntent = {},
            onArticleClick = {},
        )
    }
}
