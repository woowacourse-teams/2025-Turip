package com.on.turip.ui.compose.mypage.component

import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.on.turip.R
import com.on.turip.domain.bookmark.BookmarkContent
import com.on.turip.domain.content.Content
import com.on.turip.domain.content.video.VideoData
import com.on.turip.domain.creator.Creator
import com.on.turip.domain.region.City
import com.on.turip.domain.trip.TripDuration
import com.on.turip.ui.common.TuripUrlConverter
import com.on.turip.ui.common.mapper.toUiModel
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

@Composable
fun BookmarkedContentItem(
    item: BookmarkContent,
    onContentClick: (contentId: Long) -> Unit,
    onRemoveBookmark: (contentId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .clip(TuripTheme.shape.container)
                .clickable { onContentClick(item.content.id) }
                .border(1.dp, TuripTheme.colors.border, TuripTheme.shape.container)
                .padding(TuripTheme.spacing.extraSmall),
    ) {
        ContentThumbnail(
            imageUrl = item.content.videoData.url,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.medium))

        Text(
            text = item.content.videoData.title,
            style = TuripTheme.typography.title2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = TuripTheme.spacing.extraSmall),
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.small))

        ContentInformation(
            item = item,
            onRemoveBookmark = onRemoveBookmark,
        )
    }
}

@Composable
private fun ContentThumbnail(
    imageUrl: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val shape = TuripTheme.shape.container
    val parsedUrl = TuripUrlConverter.convertVideoThumbnailUrl(imageUrl)

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(shape)
                .border(
                    width = 1.dp,
                    color = TuripTheme.colors.gray01,
                    shape = shape,
                ),
    ) {
        AsyncImage(
            model =
                ImageRequest
                    .Builder(LocalContext.current)
                    .data(parsedUrl)
                    .crossfade(true)
                    .build(),
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize(),
            error = painterResource(R.drawable.bg_image_placeholder),
        )
    }
}

@Composable
private fun ContentInformation(
    item: BookmarkContent,
    onRemoveBookmark: (contentId: Long) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(start = TuripTheme.spacing.extraSmall),
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.small),
        ) {
            Text(
                text =
                    stringResource(
                        R.string.region_result_video_description,
                        item.content.creator.channelName,
                        item.content.videoData.uploadedDate,
                    ),
                style = TuripTheme.typography.info2,
                color = TuripTheme.colors.gray03,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.small),
            ) {
                ContentInfoItem(
                    text = item.tripDuration.toUiModel().toDisplayText(LocalContext.current),
                    iconPainterRes = R.drawable.ic_calendar,
                )
                ContentInfoItem(
                    text = stringResource(R.string.all_total_place_count, item.tripPlaceCount),
                    iconPainterRes = R.drawable.ic_place,
                )
            }
        }
        IconButton(
            onClick = { onRemoveBookmark(item.content.id) },
            modifier = Modifier.size(48.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.btn_bookmark_selected),
                contentDescription = null,
                tint = TuripTheme.colors.primary,
            )
        }
    }
}

@Composable
private fun ContentInfoItem(
    text: String,
    @DrawableRes iconPainterRes: Int,
    modifier: Modifier = Modifier,
    textColor: Color = TuripTheme.colors.gray03,
    iconTint: Color = TuripTheme.colors.gray02,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.extraSmall),
        modifier = modifier,
    ) {
        Icon(
            painter = painterResource(iconPainterRes),
            tint = iconTint,
            contentDescription = null,
            modifier = Modifier.size(12.dp),
        )

        Text(
            text = text,
            style = TuripTheme.typography.info2,
            color = textColor,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BookmarkedContentItemPreview() {
    val content =
        BookmarkContent(
            content =
                Content(
                    1L,
                    Creator(1L, "채널명", ""),
                    VideoData("콘텐츠 제목이 길면 ...으로 표시되는 것을 확인 ㅇㅇㅇ", "thumbnail", "1박 2일"),
                    City(""),
                    true,
                ),
            tripDuration = TripDuration(1, 2),
            tripPlaceCount = 2,
        )
    TuripTheme {
        BookmarkedContentItem(
            item = content,
            onContentClick = {},
            onRemoveBookmark = {},
            modifier = Modifier.width(280.dp),
        )
    }
}
