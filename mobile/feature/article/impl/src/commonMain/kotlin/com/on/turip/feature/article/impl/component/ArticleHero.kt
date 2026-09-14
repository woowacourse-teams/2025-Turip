package com.on.turip.feature.article.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.article_author_withdrawn
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.feature.article.impl.model.ArticleDetailUiModel
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

/**
 * 히어로 높이 = 화면 폭 × 0.62. `aspectRatio` 로 표현하면 폭에서 자동으로 따라온다.
 */
const val HERO_HEIGHT_RATIO = 0.62f

private const val GRADIENT_START_RATIO = 0.45f

@Composable
fun ArticleHero(
    article: ArticleDetailUiModel,
    modifier: Modifier = Modifier,
) {
    val authorDisplayName: String =
        article.authorName ?: stringResource(Res.string.article_author_withdrawn)

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .aspectRatio(1f / HERO_HEIGHT_RATIO)
                .background(TuripTheme.colors.gray01),
    ) {
        AsyncImage(
            model = article.heroImageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colorStops =
                                arrayOf(
                                    0f to Color.Transparent,
                                    GRADIENT_START_RATIO to Color.Transparent,
                                    1f to TuripTheme.colors.black.copy(alpha = 0.75f),
                                ),
                        ),
                    ),
        )

        Column(
            modifier =
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(TuripTheme.spacing.extraLarge),
        ) {
            Text(
                text = article.tagsText,
                color = TuripTheme.colors.white,
                style = TuripTheme.typography.info1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(TuripTheme.spacing.small))

            Text(
                text = article.title,
                color = TuripTheme.colors.white,
                style = TuripTheme.typography.display,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )

            if (article.subtitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(TuripTheme.spacing.extraSmall))

                Text(
                    text = article.subtitle,
                    color = TuripTheme.colors.white,
                    style = TuripTheme.typography.body1,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(modifier = Modifier.height(TuripTheme.spacing.small))

            Text(
                text = "$authorDisplayName · ${article.publishedDate}",
                color = TuripTheme.colors.white,
                style = TuripTheme.typography.info1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ArticleHeroPreview() {
    TuripTheme {
        ArticleHero(
            article =
                ArticleDetailUiModel(
                    id = 1L,
                    title = "겨울 바다가 보고 싶다면, 속초에서 보내는 1박 2일",
                    subtitle = "속초 여행 첫걸음",
                    content = "",
                    tags = persistentListOf("속초", "겨울여행"),
                    heroImageUrl = "",
                    authorName = "튜립 에디터",
                    publishedDate = "2026-08-21",
                    places = persistentListOf(),
                ),
        )
    }
}
