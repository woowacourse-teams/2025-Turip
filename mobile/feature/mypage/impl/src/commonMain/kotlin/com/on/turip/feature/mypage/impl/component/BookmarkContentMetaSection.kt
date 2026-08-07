package com.on.turip.feature.mypage.impl.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.all_total_place_count
import com.on.turip.core.designsystem.generated.resources.ic_calendar
import com.on.turip.core.designsystem.generated.resources.ic_place
import com.on.turip.core.designsystem.generated.resources.region_result_video_description
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.model.bookmark.BookmarkContent
import com.on.turip.core.model.content.Content
import com.on.turip.core.model.content.video.VideoData
import com.on.turip.core.model.creator.Creator
import com.on.turip.core.model.region.City
import com.on.turip.core.model.trip.TripDuration
import com.on.turip.core.ui.util.formatResource
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

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
                    stringResource(Res.string.region_result_video_description)
                        .formatResource(item.content.creator.channelName, item.content.videoData.uploadedDate),
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
                    text = item.tripDuration.toDisplayText(),
                    iconPainterRes = Res.drawable.ic_calendar,
                )
                ContentInfoItem(
                    text = stringResource(Res.string.all_total_place_count).formatResource(item.tripPlaceCount),
                    iconPainterRes = Res.drawable.ic_place,
                )
            }
        }
        IconButton(
            onClick = { onRemoveBookmark(item.content.id) },
            modifier = Modifier.size(48.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Bookmark,
                contentDescription = null,
                tint = TuripTheme.colors.primary,
            )
        }
    }
}

@Composable
private fun ContentInfoItem(
    text: String,
    iconPainterRes: DrawableResource,
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

private fun TripDuration.toDisplayText(): String =
    when {
        nights == 0 && days <= 1 -> "당일치기"
        nights == 0 -> "${days}일"
        else -> "${nights}박 ${days}일"
    }

@Preview(showBackground = true)
@Composable
private fun BookmarkContentMetaSectionPreview() {
    TuripTheme {
        val content =
            BookmarkContent(
                bookmarkId = 1L,
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
                createdAt = "2026-03-04",
            )

        BookmarkContentMetaSection(
            item = content,
            onRemoveBookmark = {},
            modifier = Modifier.padding(TuripTheme.spacing.large),
        )
    }
}
