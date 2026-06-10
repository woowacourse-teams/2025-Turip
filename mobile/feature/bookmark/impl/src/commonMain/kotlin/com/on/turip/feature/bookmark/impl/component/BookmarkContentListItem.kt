package com.on.turip.feature.bookmark.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import com.on.turip.core.model.bookmark.BookmarkContent
import com.on.turip.core.model.content.Content
import com.on.turip.core.model.content.video.VideoData
import com.on.turip.core.model.creator.Creator
import com.on.turip.core.model.region.City
import com.on.turip.core.model.trip.TripDuration
import com.on.turip.core.designsystem.theme.TuripTheme

@Composable
fun BookmarkContentListItem(
    content: BookmarkContent,
    onContentClick: (contentId: Long) -> Unit,
    onRemoveBookmark: (contentId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(TuripTheme.shape.container)
                .clickable { onContentClick(content.content.id) }
                .background(TuripTheme.colors.white)
                .padding(TuripTheme.spacing.extraSmall),
    ) {
        ContentThumbnail(
            imageUrl = content.content.videoData.url,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.medium))

        BookmarkContentTitleRow(
            title = content.content.videoData.title,
            trailing = {
                BookmarkRegionChip(
                    regionName = content.content.city.name,
                    modifier = Modifier.padding(horizontal = TuripTheme.spacing.small),
                )
            },
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.small))

        BookmarkContentMetaSection(
            item = content,
            onRemoveBookmark = onRemoveBookmark,
        )
    }
}

@Composable
private fun BookmarkRegionChip(
    regionName: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .wrapContentSize()
                .background(
                    color = TuripTheme.colors.chipBackground,
                    shape = TuripTheme.shape.chip,
                ).padding(
                    horizontal = TuripTheme.spacing.medium,
                    vertical = TuripTheme.spacing.extraSmall,
                ),
    ) {
        Text(
            text = regionName,
            style = TuripTheme.typography.info1,
            color = TuripTheme.colors.black,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BookmarkContentListItemPreview() {
    TuripTheme {
        val content =
            BookmarkContent(
                1L,
                content =
                    Content(
                        1L,
                        Creator(1L, "채널명", ""),
                        VideoData("콘텐츠 제목이 길면 ...으로 표시되는 것을 확인 ㅇㅇㅇ", "thumbnail", "2026-02-12"),
                        City("대구"),
                        true,
                    ),
                tripDuration = TripDuration(1, 2),
                tripPlaceCount = 2,
                createdAt = "",
            )
        BookmarkContentListItem(
            content = content,
            onContentClick = {},
            onRemoveBookmark = {},
            modifier =
                Modifier
                    .padding(TuripTheme.spacing.large)
                    .background(TuripTheme.colors.white),
        )
    }
}
