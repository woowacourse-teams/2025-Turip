package com.on.turip.feature.bookmark.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.model.Content

@Composable
fun BookmarkContentItem(
    content: Content,
    onItemClick: (contentId: Long) -> Unit,
    onRemoveBookmark: (contentId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemClick(content.id) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Thumbnail(
            thumbnailUrl = content.videoData.thumbnailUrl,
            modifier = Modifier
                .size(width = 100.dp, height = 72.dp),
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = content.title,
                style = TuripTheme.typography.body2,
                color = TuripTheme.colors.black,
                maxLines = 2,
            )
            Text(
                text = content.creator.name,
                style = TuripTheme.typography.info1,
                color = TuripTheme.colors.gray03,
            )
            if (content.places.isNotEmpty()) {
                Text(
                    text = "장소 ${content.places.size}곳",
                    style = TuripTheme.typography.info1,
                    color = TuripTheme.colors.gray03,
                )
            }
        }

        IconButton(
            onClick = { onRemoveBookmark(content.id) },
        ) {
            Icon(
                imageVector = Icons.Filled.Bookmark,
                contentDescription = "북마크 제거",
                tint = TuripTheme.colors.primary,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Composable
private fun Thumbnail(
    thumbnailUrl: String?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(TuripTheme.colors.gray01),
    ) {
        if (thumbnailUrl != null) {
            AsyncImage(
                model = thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize(),
            )
        }
    }
}
