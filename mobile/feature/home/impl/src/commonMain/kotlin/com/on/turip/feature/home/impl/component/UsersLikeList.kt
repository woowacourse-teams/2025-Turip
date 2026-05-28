package com.on.turip.feature.home.impl.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.model.UsersLikeContent

@Composable
fun UsersLikeList(
    usersLikeContents: List<UsersLikeContent>,
    onContentClick: (contentId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.wrapContentHeight(),
        contentPadding = PaddingValues(end = TuripTheme.spacing.small),
        horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.medium),
    ) {
        items(usersLikeContents, key = { it.content.id }) { item ->
            UsersLikeItem(
                thumbnailUrl = item.content.videoData.thumbnailUrl,
                title = item.content.title,
                channelName = item.content.creator.name,
                tripDuration = item.tripDuration,
                modifier = Modifier
                    .width(280.dp)
                    .clip(TuripTheme.shape.chip)
                    .clickable { onContentClick(item.content.id) },
            )
        }
    }
}
