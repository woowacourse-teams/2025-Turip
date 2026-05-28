package com.on.turip.feature.mypage.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.model.Content
import kotlinx.collections.immutable.ImmutableList

@Composable
fun MyPageBookmarkSection(
    bookmarks: ImmutableList<Content>,
    isLoading: Boolean,
    isError: Boolean,
    onViewAll: () -> Unit,
    onContentClick: (contentId: Long) -> Unit,
    onRemoveBookmark: (contentId: Long) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        BookmarkHeader(
            hasBookmarks = bookmarks.isNotEmpty(),
            onViewAll = onViewAll,
            modifier = Modifier.padding(start = TuripTheme.spacing.large),
        )

        when {
            isError -> BookmarkError(onRetry = onRetry)
            isLoading -> BookmarkLoading()
            bookmarks.isEmpty() -> BookmarkEmpty()
            else -> LazyRow(
                contentPadding = PaddingValues(end = TuripTheme.spacing.medium),
                horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.large),
                modifier = Modifier.padding(start = TuripTheme.spacing.large),
            ) {
                items(items = bookmarks, key = { it.id }) { content ->
                    BookmarkItem(
                        content = content,
                        onContentClick = onContentClick,
                        onRemoveBookmark = onRemoveBookmark,
                        modifier = Modifier.width(280.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun BookmarkHeader(
    hasBookmarks: Boolean,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = "북마크한 콘텐츠",
            style = TuripTheme.typography.title2,
        )

        if (hasBookmarks) {
            TextButton(
                onClick = onViewAll,
                modifier = Modifier.padding(end = TuripTheme.spacing.small),
            ) {
                Text(
                    text = "전체보기",
                    color = TuripTheme.colors.primary,
                    style = TuripTheme.typography.info1,
                )
            }
        }
    }
}

@Composable
private fun BookmarkError(onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp),
    ) {
        Text(
            text = "불러오기에 실패했어요",
            style = TuripTheme.typography.title1,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.large))

        OutlinedButton(
            onClick = onRetry,
            shape = TuripTheme.shape.wideButton,
            border = BorderStroke(width = 1.dp, color = TuripTheme.colors.primary),
            contentPadding = PaddingValues(
                horizontal = TuripTheme.spacing.large,
                vertical = TuripTheme.spacing.small,
            ),
        ) {
            Icon(
                imageVector = Icons.Outlined.Refresh,
                contentDescription = null,
                tint = TuripTheme.colors.primary,
            )
            Spacer(modifier = Modifier.width(TuripTheme.spacing.small))
            Text(
                text = "다시 시도",
                style = TuripTheme.typography.title2,
                color = TuripTheme.colors.primary,
            )
        }
    }
}

@Composable
private fun BookmarkLoading(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp),
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(36.dp),
            color = TuripTheme.colors.gray03,
        )
    }
}

@Composable
private fun BookmarkEmpty(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth(),
    ) {
        Spacer(modifier = Modifier.height(TuripTheme.spacing.huge))

        Text(
            text = "북마크한 콘텐츠가 없어요",
            style = TuripTheme.typography.title2,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.extraHuge))
    }
}

@Composable
private fun BookmarkItem(
    content: Content,
    onContentClick: (contentId: Long) -> Unit,
    onRemoveBookmark: (contentId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .border(1.dp, TuripTheme.colors.border, TuripTheme.shape.container)
            .clip(TuripTheme.shape.container)
            .clickable { onContentClick(content.id) }
            .background(TuripTheme.colors.white)
            .padding(TuripTheme.spacing.extraSmall),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(TuripTheme.colors.gray01),
        ) {
            content.videoData.thumbnailUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = content.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        Spacer(modifier = Modifier.height(TuripTheme.spacing.medium))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = content.title,
                style = TuripTheme.typography.info1,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            IconButton(
                onClick = { onRemoveBookmark(content.id) },
                modifier = Modifier.size(32.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Bookmark,
                    contentDescription = "북마크 삭제",
                    tint = TuripTheme.colors.primary,
                )
            }
        }

        Text(
            text = content.creator.name,
            style = TuripTheme.typography.info2,
            color = TuripTheme.colors.gray03,
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.small))
    }
}
