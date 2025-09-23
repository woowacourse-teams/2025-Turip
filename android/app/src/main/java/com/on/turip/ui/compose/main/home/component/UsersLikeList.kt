package com.on.turip.ui.compose.main.home.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.ui.common.TuripUrlConverter
import com.on.turip.ui.common.model.trip.toDisplayText
import com.on.turip.ui.main.home.UsersLikeContentModel

@Composable
fun UsersLikeList(
    usersLikeContents: List<UsersLikeContentModel>,
    onContentClick: (UsersLikeContentModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.wrapContentHeight(),
        contentPadding = PaddingValues(end = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(
            usersLikeContents,
            key = { it.content.id },
        ) { usersLikeContent: UsersLikeContentModel ->
            UsersLikeItem(
                thumbnailUrl = TuripUrlConverter.convertVideoThumbnailUrl(usersLikeContent.content.videoData.url),
                regionName = usersLikeContent.content.city.name,
                title = usersLikeContent.content.videoData.title,
                channelName = usersLikeContent.content.creator.channelName,
                contentDescription =
                    stringResource(
                        R.string.all_video_description,
                        usersLikeContent.content.videoData.uploadedDate,
                        usersLikeContent.tripDuration.toDisplayText(LocalContext.current),
                    ),
                modifier = Modifier.clickable { onContentClick(usersLikeContent) },
            )
        }
    }
}
