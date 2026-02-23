package com.on.turip.ui.compose.mypage.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.domain.bookmark.BookmarkContent
import com.on.turip.domain.content.Content
import com.on.turip.domain.content.video.VideoData
import com.on.turip.domain.creator.Creator
import com.on.turip.domain.region.City
import com.on.turip.domain.trip.TripDuration
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.mypage.MyPageSectionState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun BookmarkedContentSection(
    state: MyPageSectionState<ImmutableList<BookmarkContent>>,
    onViewAllContentClick: () -> Unit,
    onContentClick: (contentId: Long) -> Unit,
    onRemoveBookmark: (contentId: Long) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        val hasBookmarkedContent =
            (state as? MyPageSectionState.Success)?.data?.isNotEmpty() == true

        BookmarkedContentHeader(
            hasBookmarkedContent = hasBookmarkedContent,
            onViewAllContentClick = onViewAllContentClick,
            modifier = Modifier.padding(start = TuripTheme.spacing.large),
        )

        when (state) {
            MyPageSectionState.Error -> {
                BookmarkedContentError(onRetry = onRetry)
            }

            MyPageSectionState.Loading -> {
                BookmarkedContentLoading()
            }

            is MyPageSectionState.Success -> {
                if (hasBookmarkedContent) {
                    LazyRow(
                        contentPadding = PaddingValues(end = TuripTheme.spacing.medium),
                        horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.large),
                        modifier = Modifier.padding(start = TuripTheme.spacing.large),
                    ) {
                        items(items = state.data, key = { it.content.id }) {
                            BookmarkedContentItem(
                                item = it,
                                onContentClick = onContentClick,
                                onRemoveBookmark = onRemoveBookmark,
                                modifier = Modifier.width(280.dp),
                            )
                        }
                    }
                } else {
                    EmptyBookmarkedContent()
                }
            }
        }
    }
}

@Composable
private fun BookmarkedContentHeader(
    hasBookmarkedContent: Boolean,
    onViewAllContentClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = stringResource(R.string.my_page_bookmark_content_title),
            style = TuripTheme.typography.title2,
        )

        if (hasBookmarkedContent) {
            TextButton(
                onClick = onViewAllContentClick,
                modifier = Modifier.padding(end = TuripTheme.spacing.small),
            ) {
                Text(
                    text = stringResource(R.string.my_page_view_all_content),
                    color = TuripTheme.colors.primary,
                    style = TuripTheme.typography.info1,
                )
            }
        }
    }
}

@Composable
private fun BookmarkedContentError(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp),
    ) {
        Text(
            text = stringResource(R.string.my_page_error),
            style = TuripTheme.typography.title1,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.large))

        OutlinedButton(
            onClick = onRetry,
            shape = TuripTheme.shape.wideButton,
            border =
                BorderStroke(
                    width = 1.dp,
                    color = TuripTheme.colors.primary,
                ),
            contentPadding =
                PaddingValues(
                    horizontal = TuripTheme.spacing.large,
                    vertical = TuripTheme.spacing.small,
                ),
        ) {
            Icon(
                imageVector = Icons.Outlined.Refresh,
                contentDescription = null,
                tint = TuripTheme.colors.primary,
            )

            Spacer(modifier = Modifier.width(TuripTheme.spacing.small))

            Text(
                text = stringResource(R.string.retry),
                style = TuripTheme.typography.title2,
                color = TuripTheme.colors.primary,
            )
        }
    }
}

@Composable
private fun BookmarkedContentLoading(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp),
    ) {
        CircularProgressIndicator(
            modifier =
                Modifier
                    .size(36.dp)
                    .align(Alignment.Center),
            color = TuripTheme.colors.gray03,
        )
    }
}

@Composable
private fun EmptyBookmarkedContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Spacer(modifier = Modifier.height(TuripTheme.spacing.huge))

        Image(
            painter = painterResource(R.drawable.mascot),
            contentDescription = stringResource(R.string.all_mascot_description),
            modifier = Modifier.size(70.dp),
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.large))

        Text(
            text = stringResource(R.string.my_page_empty_bookmark_content),
            style = TuripTheme.typography.title2,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.extraHuge))
    }
}

@Preview(showBackground = true, name = "북마크한 컨텐츠 없음 ")
@Composable
private fun BookmarkedContentSectionEmptyPreview() {
    TuripTheme {
        BookmarkedContentSection(
            state = MyPageSectionState.Success(persistentListOf()),
            onViewAllContentClick = {},
            onContentClick = {},
            onRemoveBookmark = {},
            onRetry = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(showBackground = true, name = "컨텐츠 존재")
@Composable
private fun BookmarkedContentSectionWithItemsPreview() {
    TuripTheme {
        BookmarkedContentSection(
            state =
                MyPageSectionState.Success(
                    persistentListOf(
                        BookmarkContent(
                            content =
                                Content(
                                    1L,
                                    Creator(1L, "채널명", ""),
                                    VideoData(
                                        "콘텐츠 제목이 길면 ...으로 표시되는 것을 확인 ㅇㅇㅇ",
                                        "thumbnail",
                                        "2026-01-02",
                                    ),
                                    City(""),
                                    true,
                                ),
                            tripDuration = TripDuration(1, 2),
                            tripPlaceCount = 2,
                        ),
                    ),
                ),
            onViewAllContentClick = {},
            onContentClick = {},
            onRemoveBookmark = {},
            onRetry = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(showBackground = true, name = "로딩 중")
@Composable
private fun BookmarkedContentSectionLoadingPreview() {
    TuripTheme {
        BookmarkedContentSection(
            state = MyPageSectionState.Loading,
            onViewAllContentClick = {},
            onContentClick = {},
            onRemoveBookmark = {},
            onRetry = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(showBackground = true, name = "에러")
@Composable
private fun BookmarkedContentSectionErrorPreview() {
    TuripTheme {
        BookmarkedContentSection(
            state = MyPageSectionState.Error,
            onViewAllContentClick = {},
            onContentClick = {},
            onRemoveBookmark = {},
            onRetry = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
