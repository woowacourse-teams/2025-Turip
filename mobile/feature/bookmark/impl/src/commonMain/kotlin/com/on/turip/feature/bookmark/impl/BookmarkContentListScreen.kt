package com.on.turip.feature.bookmark.impl

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.*
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.model.bookmark.BookmarkContent
import com.on.turip.core.model.content.Content
import com.on.turip.core.model.content.video.VideoData
import com.on.turip.core.model.creator.Creator
import com.on.turip.core.model.region.City
import com.on.turip.core.model.trip.TripDuration
import com.on.turip.core.ui.util.formatResource
import com.on.turip.feature.bookmark.impl.component.BookmarkContentListAppBar
import com.on.turip.feature.bookmark.impl.component.BookmarkContentListItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun BookmarkContentListScreen(
    onBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToContent: (contentId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(TuripTheme.colors.white)
                .systemBarsPadding(),
    ) {
        BookmarkContentListAppBar(onBackClick = onBack)
        BookmarkContentListContent(
            contents = sampleBookmarkContents,
            totalBookmarkCount = sampleBookmarkContents.size,
            onContentClick = onNavigateToContent,
            onBookmarkClick = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun BookmarkContentListContent(
    contents: ImmutableList<BookmarkContent>,
    totalBookmarkCount: Int?,
    onContentClick: (contentId: Long) -> Unit,
    onBookmarkClick: (contentId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (contents.isEmpty()) {
        BookmarkContentListEmpty(modifier = modifier)
        return
    }

    val totalBookmarkCountText =
        if (totalBookmarkCount != null) {
            stringResource(Res.string.bookmark_content_count).formatResource(totalBookmarkCount)
        } else {
            stringResource(Res.string.bookmark_content_count_fail)
        }

    Column(modifier = modifier) {
        Text(
            text = totalBookmarkCountText,
            textAlign = TextAlign.End,
            style = TuripTheme.typography.info2,
            color = TuripTheme.colors.gray03,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = TuripTheme.spacing.medium)
                    .padding(end = TuripTheme.spacing.large),
        )

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 5.dp,
            color = TuripTheme.colors.gray01,
        )

        LazyColumn(
            contentPadding = PaddingValues(TuripTheme.spacing.medium),
            modifier = Modifier.weight(1f),
        ) {
            itemsIndexed(
                items = contents,
                key = { _, item -> item.bookmarkId },
            ) { index, content ->
                BookmarkContentListItem(
                    content = content,
                    onContentClick = onContentClick,
                    onRemoveBookmark = onBookmarkClick,
                )

                if (index != contents.lastIndex) {
                    HorizontalDivider(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = TuripTheme.spacing.medium),
                        thickness = 1.dp,
                        color = TuripTheme.colors.gray01,
                    )
                }
            }
        }
    }
}

@Composable
private fun BookmarkContentListEmpty(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize(),
    ) {
        Spacer(modifier = Modifier.height(TuripTheme.spacing.huge))

        Image(
            painter = painterResource(Res.drawable.mascot),
            contentDescription = stringResource(Res.string.all_mascot_description),
            modifier = Modifier.size(70.dp),
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.large))

        Text(
            text = stringResource(Res.string.my_page_empty_bookmark_content),
            style = TuripTheme.typography.title2,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.extraHuge))
    }
}

private val sampleBookmarkContents: ImmutableList<BookmarkContent> =
    persistentListOf(
        BookmarkContent(
            bookmarkId = 1L,
            content =
                Content(
                    1L,
                    Creator(1L, "채널명", ""),
                    VideoData("콘텐츠 제목이 길면 말줄임 처리되는 것을 확인", "thumbnail", "2026-02-12"),
                    City("대구"),
                    true,
                ),
            tripDuration = TripDuration(1, 2),
            tripPlaceCount = 2,
            createdAt = "",
        ),
        BookmarkContent(
            bookmarkId = 2L,
            content =
                Content(
                    2L,
                    Creator(2L, "Turip", ""),
                    VideoData("서울 여행 코스", "", "2026-02-13"),
                    City("서울"),
                    true,
                ),
            tripDuration = TripDuration(0, 1),
            tripPlaceCount = 5,
            createdAt = "",
        ),
    )

@Preview(showBackground = true)
@Composable
private fun BookmarkContentListScreenPreview() {
    TuripTheme {
        BookmarkContentListScreen(
            onBack = {},
            onNavigateToLogin = {},
            onNavigateToContent = {},
        )
    }
}
