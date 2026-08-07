package com.on.turip.feature.trip.impl.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.component.TuripAppBar
import com.on.turip.core.designsystem.generated.resources.*
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.theme.TuripTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun TripDetailAppBar(
    isError: Boolean,
    isLoading: Boolean,
    isBookmarked: Boolean,
    onBackClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bookmarkIcon = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder

    TuripAppBar(
        contentColor = TuripTheme.colors.white,
        containerColor = TuripTheme.colors.primary,
        start = {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = stringResource(Res.string.all_back_description),
                modifier = Modifier.clickable(onClick = onBackClick),
            )
        },
        end = {
            if (!isError && !isLoading) {
                IconButton(
                    onClick = onBookmarkClick,
                    modifier =
                        Modifier
                            .size(48.dp)
                            .offset(x = 10.dp),
                ) {
                    Icon(
                        imageVector = bookmarkIcon,
                        contentDescription = stringResource(Res.string.trip_detail_bookmark_description),
                        modifier = Modifier.size(28.dp),
                    )
                }
            }
        },
        modifier = modifier,
    )
}

@Preview(showBackground = true, name = "컨텐츠 북마크 선택 상태")
@Composable
private fun IsBookmarkedTripDetailAppBarPreview() {
    TuripTheme {
        TripDetailAppBar(
            isError = false,
            isLoading = false,
            isBookmarked = true,
            onBackClick = { },
            onBookmarkClick = { },
        )
    }
}

@Preview(showBackground = true, name = "컨텐츠 북마크 해제 상태")
@Composable
private fun IsNotBookmarkedTripDetailAppBarPreview() {
    TuripTheme {
        TripDetailAppBar(
            isError = false,
            isBookmarked = false,
            onBackClick = { },
            onBookmarkClick = { },
            isLoading = false,
        )
    }
}

@Preview(showBackground = true, name = "화면에 에러 발생")
@Composable
private fun ErrorTripDetailAppBarPreview() {
    TuripTheme {
        TripDetailAppBar(
            isError = true,
            isBookmarked = false,
            onBackClick = { },
            onBookmarkClick = { },
            isLoading = true,
        )
    }
}
