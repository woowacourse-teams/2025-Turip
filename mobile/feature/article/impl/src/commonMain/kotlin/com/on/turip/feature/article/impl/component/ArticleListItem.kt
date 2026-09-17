package com.on.turip.feature.article.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.article_author_withdrawn
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.feature.article.impl.model.ArticleUiModel
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

private const val THUMBNAIL_ASPECT_RATIO = 16f / 9f

/**
 * 아티클 목록 한 줄. 항목 전체가 하나의 클릭 영역이다.
 */
@Composable
fun ArticleListItem(
    article: ArticleUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(TuripTheme.shape.container)
                .clickable(onClick = onClick)
                .background(TuripTheme.colors.white)
                .padding(TuripTheme.spacing.extraSmall),
    ) {
        AsyncImage(
            model = article.thumbnailUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(THUMBNAIL_ASPECT_RATIO)
                    .clip(TuripTheme.shape.chip)
                    .background(TuripTheme.colors.gray01),
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.medium))

        ArticleTags(tags = article.tags)

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

        Spacer(modifier = Modifier.height(TuripTheme.spacing.small))

        ArticleAuthorRow(
            authorName = article.authorName,
            publishedDate = article.publishedDate,
        )
    }
}

@Composable
private fun ArticleTags(
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
private fun ArticleAuthorRow(
    authorName: String?,
    publishedDate: String,
    modifier: Modifier = Modifier,
) {
    val displayName: String = authorName ?: stringResource(Res.string.article_author_withdrawn)

    Text(
        text = "$displayName · $publishedDate",
        color = TuripTheme.colors.gray03,
        style = TuripTheme.typography.info2,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier.fillMaxWidth(),
    )
}

@Preview(showBackground = true)
@Composable
private fun ArticleListItemPreview() {
    TuripTheme {
        ArticleListItem(
            article = previewArticle(),
            onClick = {},
            modifier = Modifier.padding(TuripTheme.spacing.large),
        )
    }
}

internal fun previewArticle(): ArticleUiModel =
    ArticleUiModel(
        id = 1L,
        title = "겨울 바다가 보고 싶다면, 속초에서 보내는 1박 2일",
        subtitle = "회 말고도 즐길 게 많은 겨울 속초",
        tags = persistentListOf("속초", "겨울여행"),
        thumbnailUrl = "",
        authorName = "튜립 에디터",
        publishedDate = "2026-08-21",
    )
