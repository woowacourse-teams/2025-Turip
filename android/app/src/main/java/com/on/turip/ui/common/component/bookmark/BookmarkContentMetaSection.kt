package com.on.turip.ui.common.component.bookmark

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.domain.bookmark.BookmarkContent
import com.on.turip.domain.content.Content
import com.on.turip.domain.content.video.VideoData
import com.on.turip.domain.creator.Creator
import com.on.turip.domain.region.City
import com.on.turip.domain.trip.TripDuration
import com.on.turip.ui.common.mapper.toUiModel
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

@Composable
fun BookmarkContentMetaSection(
    item: BookmarkContent,
    onRemoveBookmark: (contentId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
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
private fun BookmarkContentMetaSectionPreview() {
    TuripTheme {
        val content =
            BookmarkContent(
                content =
                    Content(
                        1L,
                        Creator(1L, "채널명", ""),
                        VideoData("", "", "2026-02-12"),
                        City(""),
                        true,
                    ),
                tripDuration = TripDuration(0, 1),
                tripPlaceCount = 100,
            )

        BookmarkContentMetaSection(
            item = content,
            onRemoveBookmark = {},
            modifier = Modifier.padding(TuripTheme.spacing.large),
        )
    }
}
