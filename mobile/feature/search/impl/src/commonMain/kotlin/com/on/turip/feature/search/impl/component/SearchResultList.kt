package com.on.turip.feature.search.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.model.Content
import kotlinx.collections.immutable.ImmutableList

@Composable
fun SearchResultList(
    totalCount: Int,
    contents: ImmutableList<Content>,
    onItemClick: (id: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TuripTheme.colors.white),
    ) {
        Text(
            text = "검색결과 ${totalCount}건",
            style = TuripTheme.typography.body2,
            color = TuripTheme.colors.gray03,
            modifier = Modifier.padding(
                horizontal = TuripTheme.spacing.extraLarge,
                vertical = TuripTheme.spacing.medium,
            ),
        )

        LazyColumn(
            contentPadding = PaddingValues(bottom = TuripTheme.spacing.large),
            verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.large),
        ) {
            items(items = contents, key = { it.id }) { content ->
                SearchResultItem(
                    content = content,
                    onItemClick = { onItemClick(content.id) },
                )
            }
        }
    }
}

@Composable
private fun SearchResultItem(
    content: Content,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val itemShape = TuripTheme.shape.wideButton

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = TuripTheme.spacing.extraLarge, vertical = TuripTheme.spacing.small)
            .background(color = TuripTheme.colors.gray01.copy(alpha = 0.3f), shape = itemShape)
            .border(width = 1.dp, color = TuripTheme.colors.gray02, shape = itemShape)
            .clip(itemShape)
            .clickable { onItemClick() }
            .padding(TuripTheme.spacing.medium),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            AsyncImage(
                model = content.creator.profileImageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )

            Column(
                modifier = Modifier
                    .padding(start = TuripTheme.spacing.extraSmall)
                    .weight(1f),
            ) {
                Text(
                    text = content.title,
                    style = TuripTheme.typography.title2,
                    color = TuripTheme.colors.black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = content.creator.name,
                    style = TuripTheme.typography.info2,
                    color = TuripTheme.colors.gray03,
                    modifier = Modifier.padding(top = TuripTheme.spacing.extraSmall),
                )
            }
        }

        content.videoData.thumbnailUrl?.let { thumbnailUrl ->
            Spacer(modifier = Modifier.padding(top = TuripTheme.spacing.medium))
            AsyncImage(
                model = thumbnailUrl,
                contentDescription = content.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(TuripTheme.shape.container),
                contentScale = ContentScale.Crop,
            )
        }
    }
}
