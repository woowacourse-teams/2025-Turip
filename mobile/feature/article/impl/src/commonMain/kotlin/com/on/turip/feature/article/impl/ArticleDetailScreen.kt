package com.on.turip.feature.article.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.on.turip.core.designsystem.component.TuripLoadingIndicator
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.ui.component.ErrorScreen
import com.on.turip.feature.article.impl.component.ArticleContent
import com.on.turip.feature.article.impl.component.ArticleDetailAppBar
import com.on.turip.feature.article.impl.component.ArticleHero
import com.on.turip.feature.article.impl.component.ArticlePlacesSection
import com.on.turip.feature.article.impl.component.HERO_HEIGHT_RATIO
import com.on.turip.feature.article.impl.model.ArticleDetailUiModel
import com.on.turip.feature.article.impl.model.ArticlePlaceUiModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ArticleDetailScreen(
    articleId: Long,
    onBackClick: () -> Unit,
    onNavigateToLoginScreen: () -> Unit,
    onOpenUrl: (url: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ArticleDetailViewModel = koinViewModel(),
) {
    val uiState: ArticleDetailState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(articleId) {
        viewModel.initArticle(articleId)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect: ArticleDetailEffect ->
            when (effect) {
                ArticleDetailEffect.NavigateToLogin -> onNavigateToLoginScreen()
                is ArticleDetailEffect.OpenMap -> onOpenUrl(effect.url)
            }
        }
    }

    ArticleDetailContent(
        uiState = uiState,
        listState = listState,
        onIntent = viewModel::onIntent,
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@Composable
private fun ArticleDetailContent(
    uiState: ArticleDetailState,
    listState: LazyListState,
    onIntent: (ArticleDetailIntent) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier =
            modifier
                .fillMaxSize()
                .background(TuripTheme.colors.white),
    ) {
        val heroHeightPx: Float = with(LocalDensity.current) { (maxWidth * HERO_HEIGHT_RATIO).toPx() }

        // 히어로가 리스트 0번이므로, 그 위를 지나가기 전까지는 첫 아이템의 스크롤 오프셋이 곧 진행도가 된다.
        val scrollProgress: Float by remember(heroHeightPx) {
            derivedStateOf {
                val scrolled: Float =
                    if (listState.firstVisibleItemIndex > 0) {
                        heroHeightPx
                    } else {
                        listState.firstVisibleItemScrollOffset.toFloat()
                    }
                if (heroHeightPx <= 0f) 0f else (scrolled / heroHeightPx).coerceIn(0f, 1f)
            }
        }

        val article: ArticleDetailUiModel? = uiState.article

        when {
            uiState.shouldShowErrorScreen -> {
                ErrorScreen(
                    errorUiState = uiState.errorUiState,
                    onRetryClick = { onIntent(ArticleDetailIntent.Retry) },
                    modifier = Modifier.fillMaxSize().statusBarsPadding(),
                )
            }

            uiState.isLoading || article == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    TuripLoadingIndicator()
                }
            }

            else -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize().navigationBarsPadding(),
                    // 마지막 장소 카드가 화면 밑단에 붙지 않도록 리스트 아래에 여백을 둔다.
                    contentPadding = PaddingValues(bottom = TuripTheme.spacing.extraLarge),
                    verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.extraLarge),
                ) {
                    item(key = "hero") {
                        ArticleHero(article = article)
                    }

                    item(key = "content") {
                        ArticleContent(content = article.content)
                    }

                    item(key = "places") {
                        ArticlePlacesSection(
                            places = article.places,
                            onPlaceMapClick = { place: ArticlePlaceUiModel ->
                                onIntent(ArticleDetailIntent.ClickPlaceMap(place))
                            },
                            // 블록 간 간격 20dp 위에 얹어 섹션 위 여백을 24dp로 만든다.
                            modifier = Modifier.padding(top = TuripTheme.spacing.extraSmall),
                        )
                    }
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth().statusBarsPadding()) {
            ArticleDetailAppBar(
                title = article?.title.orEmpty(),
                scrollProgress = if (uiState.shouldShowContent) scrollProgress else 1f,
                onBackClick = onBackClick,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ArticleDetailContentPreview() {
    TuripTheme {
        ArticleDetailContent(
            uiState =
                ArticleDetailState(
                    articleId = 1L,
                    article = previewArticleDetail(),
                    isLoading = false,
                    isFetched = true,
                ),
            listState = rememberLazyListState(),
            onIntent = {},
            onBackClick = {},
        )
    }
}

private fun previewArticleDetail(): ArticleDetailUiModel =
    ArticleDetailUiModel(
        id = 1L,
        title = "겨울 바다가 보고 싶다면, 속초에서 보내는 1박 2일",
        subtitle = "속초 여행 첫걸음",
        content = "여행은 결국 어떤 순서로 도느냐가 하루의 인상을 정합니다.",
        tags = persistentListOf("속초", "겨울여행"),
        heroImageUrl = "",
        authorName = "튜립 에디터",
        publishedDate = "2026-08-21",
        places =
            persistentListOf(
                ArticlePlaceUiModel(1L, "속초해수욕장", "강원 속초시 조양동", "해수욕장", ""),
            ),
    )
