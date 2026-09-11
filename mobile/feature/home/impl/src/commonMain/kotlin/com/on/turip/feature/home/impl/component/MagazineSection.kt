package com.on.turip.feature.home.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.home_magazine_more
import com.on.turip.core.designsystem.generated.resources.home_magazine_more_description
import com.on.turip.core.designsystem.generated.resources.home_magazine_title
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.feature.home.impl.model.MagazineArticleModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

/**
 * 다음 카드가 약 28% 걸쳐 보이도록 하는 카드 폭 비율. 이 삐져나온 조각이 "옆으로 넘길 수 있다"는 유일한 신호다.
 */
private const val CARD_WIDTH_RATIO = 0.72f

private const val PLACEHOLDER_CARD_COUNT = 2
private val SECTION_DIVIDER_HEIGHT: Dp = 8.dp
private val MORE_ICON_SIZE: Dp = 18.dp

/**
 * 홈 지역 선택 그리드 아래에 붙는 튜립 매거진 섹션.
 *
 * 분기:
 * - 로딩 중 → 실제 카드와 같은 규격의 placeholder 2장
 * - 아티클 0건 → 섹션 자체를 그리지 않는다 (빈 상태 UI 없음)
 * - 아티클 1건 → 가로 스크롤 없이 폭을 꽉 채운 단일 카드
 * - 그 외 → 가로 스크롤 카드 목록
 *
 * 카드 폭은 하드코딩하지 않고 [BoxWithConstraints] 로 잰 가용 폭에서 계산한다.
 * 홈 화면이 좌우 여백을 이미 가진 채로 이 섹션을 감싸므로, 기준이 되는 폭은 그 여백 안쪽 폭이다.
 */
@Composable
fun MagazineSection(
    articles: ImmutableList<MagazineArticleModel>,
    isLoading: Boolean,
    onArticleClick: (articleId: Long) -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!isLoading && articles.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth()) {
        Spacer(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(SECTION_DIVIDER_HEIGHT)
                    .background(TuripTheme.colors.container),
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.large))

        MagazineSectionHeader(onMoreClick = onMoreClick)

        Spacer(modifier = Modifier.height(TuripTheme.spacing.medium))

        BoxWithConstraints {
            val cardWidth: Dp = maxWidth * CARD_WIDTH_RATIO

            when {
                isLoading -> {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.medium),
                        userScrollEnabled = false,
                    ) {
                        items(PLACEHOLDER_CARD_COUNT) {
                            MagazineArticlePlaceholderCard(modifier = Modifier.width(cardWidth))
                        }
                    }
                }

                articles.size == 1 -> {
                    val article: MagazineArticleModel = articles.first()
                    MagazineArticleCard(
                        article = article,
                        onClick = { onArticleClick(article.id) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                else -> {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.medium),
                    ) {
                        items(articles, key = { it.id }) { article: MagazineArticleModel ->
                            MagazineArticleCard(
                                article = article,
                                onClick = { onArticleClick(article.id) },
                                modifier = Modifier.width(cardWidth),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MagazineSectionHeader(
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.home_magazine_title),
            color = TuripTheme.colors.gray04,
            style = TuripTheme.typography.title1,
            modifier = Modifier.weight(1f),
        )

        Row(
            modifier =
                Modifier
                    .clip(TuripTheme.shape.container)
                    .clickable(onClick = onMoreClick)
                    .padding(
                        horizontal = TuripTheme.spacing.extraSmall,
                        vertical = TuripTheme.spacing.extraSmall,
                    ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.home_magazine_more),
                color = TuripTheme.colors.gray03,
                style = TuripTheme.typography.info1,
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = stringResource(Res.string.home_magazine_more_description),
                tint = TuripTheme.colors.gray03,
                modifier = Modifier.size(MORE_ICON_SIZE),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MagazineSectionPreview() {
    TuripTheme {
        MagazineSection(
            articles =
                persistentListOf(
                    previewMagazineArticle(),
                    previewMagazineArticle().copy(id = 2L, authorName = null),
                ),
            isLoading = false,
            onArticleClick = {},
            onMoreClick = {},
        )
    }
}

@Preview(showBackground = true, name = "1건")
@Composable
private fun MagazineSectionSingleArticlePreview() {
    TuripTheme {
        MagazineSection(
            articles = persistentListOf(previewMagazineArticle()),
            isLoading = false,
            onArticleClick = {},
            onMoreClick = {},
        )
    }
}

@Preview(showBackground = true, name = "로딩")
@Composable
private fun MagazineSectionLoadingPreview() {
    TuripTheme {
        MagazineSection(
            articles = persistentListOf(),
            isLoading = true,
            onArticleClick = {},
            onMoreClick = {},
        )
    }
}
