package com.on.turip.feature.home.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.on.turip.core.designsystem.component.SkeletonBox
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.article_author_withdrawn
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.feature.home.impl.model.MagazineArticleModel
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

private const val THUMBNAIL_ASPECT_RATIO = 16f / 9f

/**
 * 홈 매거진 섹션의 아티클 카드. 카드 전체가 하나의 클릭 영역이다.
 */
@Composable
fun MagazineArticleCard(
    article: MagazineArticleModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .clip(TuripTheme.shape.chip)
                .border(
                    width = 1.dp,
                    color = TuripTheme.colors.border,
                    shape = TuripTheme.shape.chip,
                ).background(TuripTheme.colors.white)
                .clickable(onClick = onClick),
    ) {
        MagazineArticleThumbnail(thumbnailUrl = article.thumbnailUrl)

        Column(modifier = Modifier.padding(TuripTheme.spacing.medium)) {
            MagazineArticleTags(tags = article.tags)

            Spacer(modifier = Modifier.height(TuripTheme.spacing.extraSmall))

            Text(
                text = article.title,
                color = TuripTheme.colors.black,
                style = TuripTheme.typography.title3,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(TuripTheme.spacing.small))

            Text(
                text = article.subtitle,
                color = TuripTheme.colors.gray03,
                style = TuripTheme.typography.info1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(TuripTheme.spacing.medium))

            MagazineArticleAuthorRow(
                authorName = article.authorName,
                publishedDate = article.publishedDate,
            )
        }
    }
}

@Composable
private fun MagazineArticleThumbnail(
    thumbnailUrl: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .aspectRatio(THUMBNAIL_ASPECT_RATIO)
                .clip(
                    RoundedCornerShape(
                        topStart = TuripTheme.spacing.medium,
                        topEnd = TuripTheme.spacing.medium,
                    ),
                ).background(TuripTheme.colors.gray01),
    ) {
        AsyncImage(
            model = thumbnailUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().aspectRatio(THUMBNAIL_ASPECT_RATIO),
        )
    }
}

@Composable
private fun MagazineArticleTags(
    tags: List<String>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.extraSmall),
    ) {
        tags.forEach { tag: String ->
            Text(
                text = "#$tag",
                color = TuripTheme.colors.primary,
                style = TuripTheme.typography.info2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/**
 * `작성자 · 날짜` 한 줄. [authorName] 이 null 이면 탈퇴한 작성자이므로 "탈퇴한 사용자"로 대체한다.
 */
@Composable
private fun MagazineArticleAuthorRow(
    authorName: String?,
    publishedDate: String,
    modifier: Modifier = Modifier,
) {
    val displayName: String = authorName ?: stringResource(Res.string.article_author_withdrawn)

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "$displayName · $publishedDate",
            color = TuripTheme.colors.gray03,
            style = TuripTheme.typography.info2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
    }
}

/**
 * 로딩 중에 그리는, 실제 카드와 같은 규격의 placeholder.
 */
@Composable
fun MagazineArticlePlaceholderCard(modifier: Modifier = Modifier) {
    Column(
        modifier =
            modifier
                .clip(TuripTheme.shape.chip)
                .border(
                    width = 1.dp,
                    color = TuripTheme.colors.border,
                    shape = TuripTheme.shape.chip,
                ),
    ) {
        SkeletonBox(
            shape =
                RoundedCornerShape(
                    topStart = TuripTheme.spacing.medium,
                    topEnd = TuripTheme.spacing.medium,
                ),
            modifier = Modifier.fillMaxWidth().aspectRatio(THUMBNAIL_ASPECT_RATIO),
        )

        Column(
            modifier = Modifier.padding(TuripTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.small),
        ) {
            SkeletonBox(
                shape = TuripTheme.shape.container,
                modifier = Modifier.fillMaxWidth(PLACEHOLDER_TAG_WIDTH_RATIO).height(PLACEHOLDER_LINE_HEIGHT),
            )
            SkeletonBox(
                shape = TuripTheme.shape.container,
                modifier = Modifier.fillMaxWidth().height(PLACEHOLDER_TITLE_HEIGHT),
            )
            SkeletonBox(
                shape = TuripTheme.shape.container,
                modifier = Modifier.fillMaxWidth(PLACEHOLDER_SUB_WIDTH_RATIO).height(PLACEHOLDER_LINE_HEIGHT),
            )
        }
    }
}

private const val PLACEHOLDER_TAG_WIDTH_RATIO = 0.3f
private const val PLACEHOLDER_SUB_WIDTH_RATIO = 0.6f
private val PLACEHOLDER_LINE_HEIGHT: Dp = 12.dp
private val PLACEHOLDER_TITLE_HEIGHT: Dp = 36.dp

@Preview(showBackground = true)
@Composable
private fun MagazineArticleCardPreview() {
    TuripTheme {
        MagazineArticleCard(
            article = previewMagazineArticle(),
            onClick = {},
            modifier = Modifier.width(280.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MagazineArticlePlaceholderCardPreview() {
    TuripTheme {
        MagazineArticlePlaceholderCard(modifier = Modifier.width(280.dp))
    }
}

internal fun previewMagazineArticle(): MagazineArticleModel =
    MagazineArticleModel(
        id = 1L,
        title = "겨울 바다가 보고 싶다면, 속초에서 보내는 1박 2일",
        subtitle = "회 말고도 즐길 게 많은 겨울 속초",
        tags = persistentListOf("속초", "겨울여행"),
        thumbnailUrl = "",
        authorName = "튜립 에디터",
        publishedDate = "2026-08-21",
    )
