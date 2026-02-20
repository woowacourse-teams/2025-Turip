package com.on.turip.ui.compose.mypage.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

@Composable
fun BookmarkedContentSection(
    contents: List<Int>, // 임시 preview 확인용
    onViewAllContentClick: () -> Unit,
    onContentClick: () -> Unit, // 람다 파라미터 명시 필요
    onRemoveBookmark: () -> Unit, // 람다 파라미터 명시 필요
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        val hasBookmarkedContent = contents.isNotEmpty()

        BookmarkedContentHeader(
            hasBookmarkedContent = hasBookmarkedContent,
            onViewAllContentClick = onViewAllContentClick,
            modifier = Modifier.padding(start = TuripTheme.spacing.large),
        )

        if (hasBookmarkedContent) {
            LazyRow(
                contentPadding = PaddingValues(end = TuripTheme.spacing.medium),
                horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.large),
                modifier = Modifier.padding(start = TuripTheme.spacing.large),
            ) {
                items(items = contents, key = { it }) {
                    BookmarkedContentItem(
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

@Preview(showBackground = true, name = "저장한 콘텐츠 없음")
@Composable
private fun BookmarkedContentSectionEmptyPreview() {
    TuripTheme {
        BookmarkedContentSection(
            contents = emptyList(),
            onViewAllContentClick = {},
            onContentClick = {},
            onRemoveBookmark = {},
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = TuripTheme.spacing.large),
        )
    }
}

@Preview(showBackground = true, name = "저장한 콘텐츠 존재")
@Composable
private fun BookmarkedContentSectionWithItemsPreview() {
    TuripTheme {
        BookmarkedContentSection(
            contents = listOf(1, 2, 3),
            onViewAllContentClick = {},
            onContentClick = {},
            onRemoveBookmark = {},
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = TuripTheme.spacing.large),
        )
    }
}
