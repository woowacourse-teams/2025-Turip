package com.on.turip.feature.mypage.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.model.bookmark.BookmarkContent
import com.on.turip.core.model.content.Content
import com.on.turip.core.model.content.video.VideoData
import com.on.turip.core.model.creator.Creator
import com.on.turip.core.model.region.City
import com.on.turip.core.model.trip.TripDuration

@Composable
fun MyPageBookmarkContentItem(
    item: BookmarkContent,
    onContentClick: (contentId: Long) -> Unit,
    onRemoveBookmark: (contentId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .border(1.dp, TuripTheme.colors.border, TuripTheme.shape.container)
                .clip(TuripTheme.shape.container)
                .clickable { onContentClick(item.content.id) }
                .background(TuripTheme.colors.white)
                .padding(TuripTheme.spacing.extraSmall),
    ) {
        ContentThumbnail(
            imageUrl = item.content.videoData.url,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.medium))

        BookmarkContentTitleRow(title = item.content.videoData.title)

        Spacer(modifier = Modifier.height(TuripTheme.spacing.small))

        BookmarkContentMetaSection(
            item = item,
            onRemoveBookmark = onRemoveBookmark,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MyPageBookmarkContentItemPreview() {
    val content =
        BookmarkContent(
            bookmarkId = 1L,
            content =
                Content(
                    1L,
                    Creator(1L, "채널명", ""),
                    VideoData("콘텐츠 제목이 길면 ...으로 표시되는 것을 확인 ㅇㅇㅇ", "thumbnail", "2026-02-23"),
                    City(""),
                    true,
                ),
            tripDuration = TripDuration(1, 2),
            tripPlaceCount = 2,
            createdAt = "2026-03-04",
        )
    TuripTheme {
        MyPageBookmarkContentItem(
            item = content,
            onContentClick = {},
            onRemoveBookmark = {},
            modifier =
                Modifier
                    .width(280.dp)
                    .padding(TuripTheme.spacing.large),
        )
    }
}
